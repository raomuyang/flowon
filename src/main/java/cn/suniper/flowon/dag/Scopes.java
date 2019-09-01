package cn.suniper.flowon.dag;

import java.util.List;

/**
 * The Scopes interface is in order to make the way to create a DAG more flexible,
 * through the {@link Scopes#map(Object, String)}, you can define a way to generate a
 * series vertices of DAG from input parameters,
 * and the {@link Scopes#isInclude(String, String, String)} let you can determines how to
 * describe the inclusion relationship of data URI
 *
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public interface Scopes<T> {
    /**
     * The input data should be mapped to a list of data URI through the giving {@code scope},
     * the {@code scope} needs to meet the inclusion relationship between different levels,
     * for example, we have the following inputs:
     * <ul>
     *     <li>project=p1, group=g1, file=f1, kwargs...</li>
     *     <li>project=p1, group=g1, file=f2, kwargs...</li>
     *     <li>project=p1, group=g2, file=f3, kwargs...</li>
     * </ul>
     * we can define 3 values of {@code scope}: {@code project}, {@code group} and {@code file},
     * as scope {@code project}, we can get 1 dataURI: {@code project:p1}
     * as scope {@code group}, we got 2 dataURI:
     * <ul>
     *     <li>{@code project:p1/g1}</li>
     *     <li>{@code project:p1/g2}</li>
     * </ul>
     *
     * as scope {@code file}, we can get 3 dataURI:
     * <ul>
     *     <li>{@code project:p1/g1/f1}</li>
     *     <li>{@code project:p1/g1/f2}</li>
     *     <li>{@code project:p1/g2/f3}</li>
     * </ul>
     *
     * and, the relationship meet the following requirements:
     * <ul>
     *     <li>{@code project:p1/g1} and {@code project:p1/g2} are the subset of {@code project:p1}</li>
     *     <li>{@code project:p1/g1/f1} and {@code project:p1/g1/f2} are the subset of {@code project:p1/g1}</li>
     *     <li>{@code project:p1/g2/f3} is the subset of {@code project:p1/g2}</li>
     * </ul>
     *
     * @param params input data parameters
     * @param scope the value of "scope" determine the inclusion relationship of the different dataURI
     * @return a list of dataURI to bind vertices
     */
    List<String> map(T params, String scope);

    /**
     * Determine whether {@code subDataURI} is an item of the set which the {@code dataURI} representative.
     * @param dataURI the data URI which representative of a set of resources
     * @param subDataURI the URI which might the subset of parameter {@code dataURI}
     * @return true if {@code subDataURI} is the {@code non-empty proper subset} of {@code dataURI} else false
     */
    boolean isInclude(String dataURI, String subDataURI, String dependenciesNodeName);
}
