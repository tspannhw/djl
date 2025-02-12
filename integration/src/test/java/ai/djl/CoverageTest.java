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
package ai.djl;

import ai.djl.basicdataset.Cifar10;
import ai.djl.mxnet.engine.MxEngine;
import ai.djl.mxnet.zoo.MxModelZoo;
import ai.djl.repository.Repository;
import ai.djl.test.CoverageUtils;
import ai.djl.zoo.cv.classification.ResNetV1;
import java.io.IOException;
import org.testng.annotations.Test;

public class CoverageTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        // API
        CoverageUtils.testGetterSetters(Device.class);

        // model-zoo
        CoverageUtils.testGetterSetters(ResNetV1.class);

        // repository
        CoverageUtils.testGetterSetters(Repository.class);

        // basicdataset
        CoverageUtils.testGetterSetters(Cifar10.class);

        // mxnet-engine
        CoverageUtils.testGetterSetters(MxEngine.class);

        // mxnet-model-zoo
        CoverageUtils.testGetterSetters(MxModelZoo.class);
    }
}
