/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
* Copyright (C) 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.android.cardemulation.tests;

import com.example.android.cardemulation.*;

import android.test.ActivityInstrumentationTestCase2;

/**
* Tests for CardEmulation sample.
*/
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private CardEmulationFragment mTestFragment;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        mTestActivity = getActivity();
        mTestFragment = (CardEmulationFragment)
            mTestActivity.getSupportFragmentManager().getFragments().get(1);
    }

    /**
    * Test if the test fixture has been set up correctly.
    */
    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }

    /**
     * Test converting from a hex string to binary.
     */
    public void testHexToBinary() {
        final byte[] testData = {(byte) 0xc0, (byte) 0xff, (byte) 0xee};
        final byte[] output = CardService.HexStringToByteArray("C0FFEE");
        for (int i = 0; i < testData.length; i++) {
            assertEquals(testData[i], output[i]);
        }
    }

    /**
     * Test converting from binary to a hex string
     */
    public void testBinaryToHex() {
        final byte[] input = {(byte) 0xc0, (byte) 0xff, (byte) 0xee};
        final String output = CardService.ByteArrayToHexString(input);
        assertEquals("C0FFEE", output);
    }

    /**
     * Verify that account number is returned in response to APDU.
     */
    public void testRequestAccoutNumber() {
        AccountStorage.SetAccount(this.getActivity(), "1234");
        final byte[] command = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x05,
                (byte) 0xF2, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22};
        final byte[] expectedResponse = {(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34,
                (byte) 0x90, (byte) 0x00};
        final CardService cs = new CardService();
        final byte[] output = cs.processCommandApdu(command, null);

        assertEquals(expectedResponse.length, output.length);
        for (int i = 0; i < expectedResponse.length; i++) {
            assertEquals(expectedResponse[i], output[i]);
        }

    }


    /**
     * Verify that account number is returned in response to APDU.
     */
    public void testInvalidCommand() {
        AccountStorage.SetAccount(this.getActivity(), "1234");
        final byte[] command = {(byte) 0x01, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x05,
                (byte) 0xF2, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22};
        final byte[] expectedResponse = {(byte) 0x00, (byte) 0x00};
        final CardService cs = new CardService();
        final byte[] output = cs.processCommandApdu(command, null);

        assertEquals(expectedResponse.length, output.length);
        for (int i = 0; i < expectedResponse.length; i++) {
            assertEquals(expectedResponse[i], output[i]);
        }
    }

    /**
     * Test functionality fo ConcatArrays method.
     */
    public void testArrayConcat() {
        final byte[] a = {(byte) 0x01, (byte) 0x02};
        final byte[] b = {(byte) 0x03, (byte) 0x04};
        final byte[] c = {(byte) 0x05, (byte) 0x06};
        final byte[] expected = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
                (byte) 0x06};
        final byte[] result = CardService.ConcatArrays(a, b, c);

        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }

    /**
     * Test setting and getting account number
     */
    public void testSetAccountNumber() {
        final String account = "3456";
        AccountStorage.SetAccount(getActivity(), account);
        this.assertEquals(account, AccountStorage.GetAccount(getActivity()));
    }

    /**
     * Test building SELECT APDU from AID string.
     */
    public void testBuildSelectApdu() {
        final String aid = "1234";
        final byte[] expectedResult = {(byte) 0x00, (byte) 0xA4, 04, (byte) 0x00, (byte) 0x02,
                (byte) 0x12, (byte) 0x34};
        final byte[] result = CardService.BuildSelectApdu(aid);

        assertEquals(expectedResult.length, result.length);
        for (int i = 0; i < expectedResult.length; i++) {
            assertEquals(expectedResult[i], result[i]);
        }
    }
}