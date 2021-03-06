/*
 * Copyright (C) 2019 Yubico.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yubico.yubikit;

import com.yubico.yubikit.apdu.Apdu;
import com.yubico.yubikit.apdu.ApduCodeException;
import com.yubico.yubikit.apdu.ApduUtils;
import com.yubico.yubikit.transport.Iso7816Connection;
import com.yubico.yubikit.transport.YubiKeySession;

import java.io.Closeable;
import java.io.IOException;

/**
 * Class that allows to open ISO 7816 connection to YubiKey
 * and communicate using APDUs
 */
public class Iso7816Application implements Closeable {

    /**
     * Open ISO 7816 connection to yubikey
     */
    private Iso7816Connection connection;

    /**
     * Answer to reset
     */
    private byte[] atr;

    /**
     * Create new instance of {@link Iso7816Application}
     * and selects the application for use
     *
     * @param session session with YubiKey
     * @throws IOException   in case of connection error
     */
    public Iso7816Application(YubiKeySession session) throws IOException {
        this.connection = session.openIso7816Connection();
        atr = connection.getAtr();
    }


    @Override
    public void close() throws IOException {
        connection.close();
    }

    /**
     * @return open ISO 7816 connection to yubikey
     */
    public Iso7816Connection getConnection() {
        return connection;
    }

    /**
     * Answer to reset
     *
     * @return response to identify card reader
     */
    public byte[] getAtr() {
        return atr;
    }


    /**
     * Sends APDU command and receives byte array from connection
     * In case if length of output blob is bigger than 255 than it splits into set of APDU commands
     * In case if output has status code that it has remaining info sends another APDU command to receive what's remaining
     *
     * @param command well structured command that needs to be send
     * @return data blob concatenated from all APDU commands that were sent *set of output commands and send remaining commands)
     * @throws IOException       in case of connection and communication error
     * @throws ApduCodeException in case if received error in APDU response
     */
    public byte[] sendAndReceive(Apdu command) throws IOException, ApduCodeException {
        return ApduUtils.sendAndReceive(connection, command);
    }

    /**
     * Sends APDU command and receives byte array from connection
     * In case if length of output blob is bigger than 255 than it splits into set of APDU commands
     * In case if output has status code that it has remaining info sends another APDU command to receive what's remaining
     *
     * @param command          well structured command that needs to be send
     * @param insSentRemaining instruction byte for APDU command to receive remaining data blob (default is 0xc0)
     * @return data blob concatenated from all APDU commands that were sent *set of output commands and send remaining commands)
     * @throws IOException       in case of connection and communication error
     * @throws ApduCodeException in case if received error in APDU response
     */
    protected byte[] sendAndReceive(Apdu command, byte insSentRemaining) throws IOException, ApduCodeException {
        return ApduUtils.sendAndReceive(connection, command, insSentRemaining);
    }
}
