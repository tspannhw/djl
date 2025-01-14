/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package ai.djl.training.initializer;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;

/**
 * {@code XavierInitializer} is an {@link Initializer} that performs "Xavier" initialization for
 * parameters. This initializer is designed to keep the scale of gradients roughly the same in all
 * layers.
 *
 * <p>{@code XavierInitializer} is specified by the type of random distribution({@link RandomType}),
 * the factor type({@link FactorType}), and the magnitude of the scale. By default, {@link
 * RandomType} is {@code UNIFORM} and {@link FactorType} is {@code AVG}. The initializer fills the
 * weights with random numbers in the range of \([-c, c]\), where \(c = \sqrt{\frac{3.}{0.5 *
 * (n_{in} + n_{out})}}\) where \(n_{in}\) is the number of neurons feeding into weights, and
 * \(n_{out}\) is the number of neurons the result is fed to.
 *
 * <p>If {@link RandomType} is {@code UNIFORM} and {@link FactorType} is {@code IN}, then \(c =
 * \sqrt{\frac{3.}{n_{in}}}\). Similarly when {@link FactorType} is {@code OUT}, then \(c =
 * \sqrt{\frac{3.}{n_{out}}}\).
 *
 * <p>If {@link RandomType} is {@code GAUSSIAN} and {@link FactorType} is {@code AVG}, the
 * initializer fills the weights with numbers from normal distribution with a standard deviation of
 * \(\sqrt{\frac{3.}{0.5 * (n_{in} + n_{out})}}\).
 */
public class XavierInitializer implements Initializer {
    /** Enum for different types of random distributions. */
    public enum RandomType {
        UNIFORM,
        GAUSSIAN
    }

    /** Enum for different types of factor type. */
    public enum FactorType {
        AVG,
        IN,
        OUT
    }

    private RandomType randomType;
    private FactorType factorType;
    private double magnitude;

    /**
     * Initializes a Xavier initializer.
     *
     * @param randomType the random generator type, can be GAUSSIAN or UNIFORM
     * @param factorType the factor type, can be one of AVG, IN, or OUT
     * @param magnitude the scale of the random number
     */
    public XavierInitializer(RandomType randomType, FactorType factorType, double magnitude) {
        this.randomType = randomType;
        this.factorType = factorType;
        this.magnitude = magnitude;
    }

    /** Creates a new instance of {@code XavierInitializer}. */
    public XavierInitializer() {
        this(RandomType.UNIFORM, FactorType.AVG, 3);
    }

    /** {@inheritDoc} */
    @Override
    public NDArray initialize(NDManager manager, Shape shape, DataType dataType) {

        double hwScale;
        long dimension = shape.dimension();
        if (dimension < 2) {
            throw new IllegalArgumentException(
                    "XavierInitializer cannot be applied to Shape with dimension: "
                            + dimension
                            + ", it requires shape to be at least 2D.");
        } else if (dimension == 2) {
            hwScale = 1.0;
        } else {
            Shape shapeSliced = shape.slice(2);
            hwScale = shapeSliced.size();
        }
        double fanIn = shape.get(1) * hwScale;
        double fanOut = shape.head() * hwScale;
        double factor;
        switch (factorType) {
            case AVG:
                factor = (fanIn + fanOut) / 2.0;
                break;
            case IN:
                factor = fanIn;
                break;
            case OUT:
                factor = fanOut;
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid factor type, valid types are: avg, in, out");
        }
        if (factor == 0f) {
            throw new IllegalStateException(
                    "Xavier initializer factor is 0, please check your input shape.");
        }
        double scale = StrictMath.sqrt(magnitude / factor);

        switch (randomType) {
            case UNIFORM:
                return manager.randomUniform(-scale, scale, shape, dataType, manager.getDevice());
            case GAUSSIAN:
                return manager.randomNormal(0, scale, shape, dataType, manager.getDevice());
            default:
                throw new IllegalArgumentException("Invalid randomType");
        }
    }
}
