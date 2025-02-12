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

package ai.djl.examples;

import ai.djl.engine.Engine;
import ai.djl.examples.training.transferlearning.TrainResnetWithCifar10;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TrainResNetTest {

    @Test
    public void testTrainResNet() {
        // Limit max 4 gpu for cifar10 training to make it converge faster.
        // and only train 10 batch for unit test.
        String[] args = {"-e", "2", "-g", "4", "-m", "10", "-s", "-p"};

        TrainResnetWithCifar10 test = new TrainResnetWithCifar10();
        Assert.assertTrue(test.runExample(args));
    }

    @Test
    public void testTrainResNetSymbolicNightly() {
        // this is nightly test
        if (!Boolean.getBoolean("nightly")) {
            return;
        }
        if (Engine.getInstance().getGpuCount() > 0) {
            // Limit max 4 gpu for cifar10 training to make it converge faster.
            // and only train 10 batch for unit test.
            String[] args = {"-e", "10", "-g", "4", "-s", "-p"};

            TrainResnetWithCifar10 test = new TrainResnetWithCifar10();
            Assert.assertTrue(test.runExample(args));
            Assert.assertTrue(test.getTrainingAccuracy() > .7f);
            Assert.assertTrue(test.getTrainingLoss() < .8f);
        }
    }

    @Test
    public void testTrainResNetImperativeNightly() {
        // this is nightly test
        if (!Boolean.getBoolean("nightly")) {
            return;
        }
        if (Engine.getInstance().getGpuCount() > 0) {
            // Limit max 4 gpu for cifar10 training to make it converge faster.
            // and only train 10 batch for unit test.
            String[] args = {"-e", "10", "-g", "4"};

            TrainResnetWithCifar10 test = new TrainResnetWithCifar10();
            Assert.assertTrue(test.runExample(args));
            Assert.assertTrue(test.getTrainingAccuracy() > .7f);
            Assert.assertTrue(test.getTrainingLoss() < .8f);
        }
    }
}
