# Flowon

该项目是应用在一个基因自动化分析交付的生产流程中应用DAG解决任务调度问题的归纳和抽象：

> *使用DAG（有向无环图）描述的工作流的模型，将输入数据渲染为AOV-网并协调任务的调度*

## DAG与拓扑排序的应用

在普通的程序设计中，当一个或一批事件完成时触发下一个或下一批事件的开始，通常我们会设计一个“围栏”，使用观察者模式或监听器模式对事件进行回调、观察并控制围栏的开关（比如java中用到的`CountDownLatch`），当事件越来越多、触发条件越来越复杂的时候，这种模式就愈发显得力有不逮，特别是在并发控制的表现中。

![监听-观察模型](https://raw.githubusercontent.com/raomuyang/flowon/master/doc/监听-观察模型.png)

比如下图是一个生信分析流程的Demo，如果使用回调的方式处理任务之间的触发条件，一旦中间某个步骤出错，都有可能成为难以恢复的灾难：
![生信分析中的分析流程Demo](https://raw.githubusercontent.com/raomuyang/flowon/master/doc/分析流程Demo.png)

当事件之间的触发条件复杂到一定程度时，就应该转换一下思路：使用有向无环图描述这个流程中多个事件/任务之间的依赖关系。顶点表示活动、弧边表示依赖，这些活动所构成的即为AOV-网。DAG在工作流引擎/计算框架等实际生产中有广泛的应用，如阿里云的批量计算框架，不过从阿里云Diku的使用的是带权的有向无环图。

使用AOV-网明清晰表示了整个流程的处理路径和依赖关系，并且可以协助完成复杂的并发控制。按照依赖的顺序，并行地完成AOV-网中所有的活动便简化为对AOV-网的拓扑排序结果的动态演绎：

1. 对AOV-网中所有的顶点拓扑排序
2. `控制器`按照拓扑排序中的路径获取所有可活动的顶点：将所有`完成`的顶点删除，依次遍历并过滤出无前驱的顶点
3. `调度器`不断刷新获取当前有可活动的顶点，处理顶点活动并将其标记为`完成`，直到DAG中所有的顶点都处理完成。

在这个处理过程中，顶点的处理顺序一定是一个拓扑有序序列。并且问题转化为了更简单的`生产-消费`模式:

  调度器和控制器分别为消费者和生产者，调度器只需要关注哪些任务可以调度并及时更新顶点活动状态，控制器则只需根据当前的顶点状态返回当前“入度为0”的顶点

## 参数处理：Map & Reduce

> TL;DR 本节为参数渲染为DAG的处理思考过程的总结

参数的转换是亘古不变的话题，如何简洁地将输入数据转换为一系列活动顶点构成的AOV-网又保证灵活性是需要尝试去抽象的：

![Parameters mapper](https://raw.githubusercontent.com/raomuyang/flowon/master/doc/Map和Reduce.png)

如果展开处理的话，解析的过程一定繁杂无比。针对生产中的任务类型和输入数据的观察，我对可处理的输入数据和活动类型做了如下限定：

* 我们视该输入数据(InputData)为一组资源ID（`resourceID`）构成的资源集合`R`，资源按照作用域类型`scope`分组，不同的作用域`scope`之间存在”包含“、“被包含”、“平级”等关系。
* 定义一种映射关系`M`将资源`R`转换为顶点的集合`V`: `V = R x M`
* 当前一个顶点所依赖的邻接顶点必须为同一种活动类型（当然从通用的角度看这是不合理的，可以改进）

为了明确顶点之间的依赖关系，定义顶点配置模板的元素如下：

```conf

<活动类型> {
    scope = <作用域>
    dependencies = <依赖的活动类型>
}
```

将输入数据解析为DAG的步骤如下：

1. 输入数据 -> 资源ID集合
2. Map: 迭代conf中的顶点配置模板，针对每个顶点配置模板，从资源集合中过滤出作用域等级为指定 `scope` 的资源ID列表，转换为顶点列表。
3. Reduce: 合并顶点列表，通过顶点中的`dependencies`属性和`resourceURI`之间的包含关系判断顶点是否为邻接顶点，构造DAG。顶点A依赖顶点B且顶点A的资源ID作用域包含顶点B的资源ID作用域时，则认为B到A存在弧边，依据资源ID和依赖的顶点类型的判断是否为邻接顶点的实现逻辑如下：

```java
for (Vertex v1 : vertexNodes) {
    ArcNode arc = null;
    for (Vertex v2 : vertexNodes) {
        if (v1.getIndex() == v2.getIndex()) continue;

        // action: v2 dependency v1; resource: dataURI_v1 is subset of dataURI_v2
        if (v1.getAction().equals(v2.getDependenciesNodeName())
                && scopes.isInclude(v2.getBindDataURI(), v1.getBindDataURI(), v2.getDependenciesNodeName())) {
            ArcNode arcV1ToV2 = new ArcNode(v2.getIndex(), null);
            if (arc == null) {
                arc = arcV1ToV2;
                v1.setFirstArc(arc);
            } else {
                arc.setNextArc(arcV1ToV2);
                arc = arcV1ToV2;
            }
        }
    }
}
```

### Demo

输入数据 ：

> 资源作用域类型：file, group, project

| file id | group id | project id |
|:--:|:--:|:--:|
| f1 | g1 | p1 |
| f2 | g1 | p1 |
| f3 | g2 | p1 |
| f4 | g2 | p1 |

处理流程：

![下载-分析](https://raw.githubusercontent.com/raomuyang/flowon/master/doc/下载-分析.png)

定义流程模板如下（使用HOCON配置）

```conf

// download by file
download {
  scope = file
}

// mapping by group, a group include one or may file(s)
mapping {
  scope = group
  dependencies = download  // the files of group download finished then start mapping task
}

// analysis by project
analysis {
  scope = project
  dependencies = mapping  // the mapping tasks of group finished then start the analysis task of project
}

```

配置的解析渲染过程，在flowon中已经实现，可以参考项目中的示例代码： [demo](https://raw.githubusercontent.com/raomuyang/flowon/master/src/demo)

