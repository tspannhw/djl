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
package ai.djl.integration.tests.ndarray;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NDArrayShapesManipulationOpTest {

    @Test
    public void testSplit() {
        // TODO add more test cases once MXNet split op bug is fixed
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.create(new float[] {1f, 2f, 3f, 4f});
            NDList result = array.split(2);
            Assert.assertEquals(manager.create(new float[] {1f, 2f}), result.get(0));
            Assert.assertEquals(manager.create(new float[] {3f, 4f}), result.get(1));
            result = array.split(new int[] {2});
            Assert.assertEquals(manager.create(new float[] {1f, 2f}), result.get(0));
            Assert.assertEquals(manager.create(new float[] {3f, 4f}), result.get(1));
        }
    }

    @Test
    public void testFlatten() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.create(new float[] {1f, 2f, 3f, 4f});
            NDArray actual = manager.create(new float[] {1f, 2f, 3f, 4f});
            Assert.assertEquals(actual, array.flatten());

            // multi-dim
            array = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 2));
            actual = manager.create(new float[] {1f, 2f, 3f, 4f});
            Assert.assertEquals(actual, array.flatten());

            // scalar
            array = manager.create(5f);
            actual = manager.create(new float[] {5f});
            Assert.assertEquals(actual, array.flatten());

            // zero-dim
            array = manager.create(new Shape(2, 0));
            actual = manager.create(new Shape(0));
            Assert.assertEquals(actual, array.flatten());
        }
    }

    @Test
    public void testReshape() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.create(new float[] {1f, 2f, 3f, 4f, 5f, 6f});
            NDArray actual =
                    manager.create(new float[] {1f, 2f, 3f, 4f, 5f, 6f}, new Shape(2, 1, 1, 3));
            Assert.assertEquals(actual, array.reshape(2, 1, 1, 3));
            Assert.assertEquals(actual, array.reshape(-1, 1, 1, 3));

            // multi-dim
            array = manager.create(new float[] {1f, 2f, 3f, 4f, 5f, 6f}, new Shape(3, 2));
            actual = manager.create(new float[] {1f, 2f, 3f, 4f, 5f, 6f}, new Shape(2, 3));
            Assert.assertEquals(actual, array.reshape(new Shape(2, 3)));
            Assert.assertEquals(actual, array.reshape(new Shape(2, -1)));

            // scalar
            array = manager.create(5f);
            actual = manager.create(new float[] {5f});
            Assert.assertEquals(actual, array.reshape(1));
            actual = manager.create(new float[] {5f}, new Shape(1, 1, 1));
            Assert.assertEquals(actual, array.reshape(1, -1, 1));

            // zero-dim
            array = manager.create(new Shape(1, 0));
            actual = manager.create(new Shape(2, 3, 0, 1));
            Assert.assertEquals(actual, array.reshape(2, 3, 0, 1));
        }
    }

    @Test
    public void testExpandDim() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.create(new float[] {1f, 2f});
            NDArray actual = manager.create(new float[] {1f, 2f}, new Shape(1, 2));
            Assert.assertEquals(actual, array.expandDims(0));

            // multi-dim
            array = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 2));
            actual = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 1, 2));
            Assert.assertEquals(actual, array.expandDims(1));

            // scalar
            array = manager.create(4f);
            actual = manager.create(new float[] {4f});
            Assert.assertEquals(actual, array.expandDims(0));

            // zero-dim
            array = manager.create(new Shape(2, 1, 0));
            actual = manager.create(new Shape(2, 1, 1, 0));
            Assert.assertEquals(actual, array.expandDims(2));
        }
    }

    @Test
    public void testSqueeze() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.ones(new Shape(1, 2, 1, 3, 1));
            NDArray actual = manager.ones(new Shape(2, 3));
            Assert.assertEquals(actual, array.squeeze());
            actual = manager.ones(new Shape(1, 2, 3, 1));
            Assert.assertEquals(actual, array.squeeze(2));
            actual = manager.ones(new Shape(2, 1, 3));
            Assert.assertEquals(actual, array.squeeze(new int[] {0, 4}));

            // scalar
            array = manager.create(2f);
            Assert.assertEquals(array, array.squeeze());
            Assert.assertEquals(array, array.squeeze(0));
            Assert.assertEquals(array, array.squeeze(new int[] {0}));

            // zero-dim
            array = manager.create(new Shape(1, 0, 1, 3, 1));
            actual = manager.create(new Shape(0, 3));
            Assert.assertEquals(actual, array.squeeze());
            actual = manager.create(new Shape(1, 0, 3, 1));
            Assert.assertEquals(actual, array.squeeze(2));
            actual = manager.create(new Shape(0, 1, 3));
            Assert.assertEquals(actual, array.squeeze(new int[] {0, 4}));
        }
    }

    @Test
    public void testStack() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array1 = manager.create(new float[] {1f, 2f});
            NDArray array2 = manager.create(new float[] {3f, 4f});

            NDArray actual = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 2));
            Assert.assertEquals(actual, array1.stack(array2));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2)));
            actual = manager.create(new float[] {1f, 3f, 2f, 4f}, new Shape(2, 2));
            Assert.assertEquals(actual, array1.stack(array2, 1));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2), 1));

            array1 = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 2));
            array2 = manager.create(new float[] {5f, 6f, 7f, 8f}, new Shape(2, 2));
            actual =
                    manager.create(
                            new float[] {1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f}, new Shape(2, 2, 2));
            Assert.assertEquals(actual, array1.stack(array2));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2)));
            actual =
                    manager.create(
                            new float[] {1f, 2f, 5f, 6f, 3f, 4f, 7f, 8f}, new Shape(2, 2, 2));
            Assert.assertEquals(actual, array1.stack(array2, 1));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2), 1));
            actual =
                    manager.create(
                            new float[] {1f, 5f, 2f, 6f, 3f, 7f, 4f, 8f}, new Shape(2, 2, 2));
            Assert.assertEquals(actual, array1.stack(array2, 2));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2), 2));

            // scalar
            array1 = manager.create(5f);
            array2 = manager.create(4f);
            actual = manager.create(new float[] {5f, 4f});
            Assert.assertEquals(actual, array1.stack(array2));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array2)));

            // zero-dim
            array1 = manager.create(new Shape(0, 0));
            actual = manager.create(new Shape(2, 0, 0));
            Assert.assertEquals(actual, array1.stack(array1));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array1)));
            actual = manager.create(new Shape(0, 2, 0));
            Assert.assertEquals(actual, array1.stack(array1, 1));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array1), 1));
            actual = manager.create(new Shape(0, 0, 2));
            Assert.assertEquals(actual, array1.stack(array1, 2));
            Assert.assertEquals(actual, NDArrays.stack(new NDList(array1, array1), 2));
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConcat() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array1 = manager.create(new float[] {1f});
            NDArray array2 = manager.create(new float[] {2f});
            NDArray actual = manager.create(new float[] {1f, 2f});
            //            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array2),
            // 0));
            Assert.assertEquals(actual, array1.concat(array2));

            array1 = manager.create(new float[] {1f, 2f, 3f, 4f}, new Shape(2, 2));
            array2 = manager.create(new float[] {5f, 6f, 7f, 8f}, new Shape(2, 2));
            actual = manager.create(new float[] {1f, 2f, 3f, 4f, 5f, 6f, 7, 8f}, new Shape(4, 2));
            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array2)));
            Assert.assertEquals(actual, array1.concat(array2));
            actual = manager.create(new float[] {1f, 2f, 5f, 6f, 3f, 4f, 7f, 8f}, new Shape(2, 4));
            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array2), 1));
            actual = manager.create(new float[] {1f, 2f, 5f, 6f, 3f, 4f, 7f, 8f}, new Shape(2, 4));
            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array2), 1));

            // zero-dim
            array1 = manager.create(new Shape(0, 1));
            actual = manager.create(new Shape(0, 1));
            Assert.assertEquals(actual, array1.concat(array1));
            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array1)));
            actual = manager.create(new Shape(0, 2));
            Assert.assertEquals(actual, array1.concat(array1, 1));
            Assert.assertEquals(actual, NDArrays.concat(new NDList(array1, array1), 1));

            // scalar
            array1 = manager.create(1f);
            array2 = manager.create(2f);
            array1.concat(array2);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConcatNDlist() {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array1 = manager.create(1f);
            NDArray array2 = manager.create(2f);
            NDArrays.concat(new NDList(array1, array2));
        }
    }
}
