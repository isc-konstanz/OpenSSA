/*
 * Copyright 2023-24 ISC Konstanz
 *
 * This file is part of OpenSSA.
 * For more information visit https://github.com/isc-konstanz/OpenSSA.
 *
 * OpenSSA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSSA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenSSA. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.esg.ic.ssa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class GenericAdapterSettings {

    public static final String HOST = "host";
    public static final String HOST_DEFAULT = "http://localhost";

    public static final String PORT = "port";
    public static final int PORT_DEFAULT = 9090;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";


    private final String host;
    private final int port;

    private final String username;
    private final String password;


    public GenericAdapterSettings(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private GenericAdapterSettings(String host, String port, String username, String password) {
        this(host, Integer.valueOf(port), username, password);
    }

    public GenericAdapterSettings(String username, String password) {
        this(HOST_DEFAULT, PORT_DEFAULT, username, password);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST).append(" = ").append(getHost()).append("\n");
        sb.append(PORT).append(" = ").append(getPort()).append("\n");
        sb.append(USERNAME).append(" = ").append(getUsername()).append("\n");
        sb.append(PASSWORD).append(" = ").append("*********").append("\n");
        return sb.toString();
    }

    public static GenericAdapterSettings ofProperties(Path propertiesPath) throws FileNotFoundException, IOException {
    	Properties properties = new Properties();
    	properties.load(new FileInputStream(propertiesPath.toFile()));
    	return ofProperties(properties);
    }

    public static GenericAdapterSettings ofProperties(Properties properties) {
    	return new GenericAdapterSettings(
    			properties.getProperty(HOST, HOST_DEFAULT),
    			properties.getProperty(PORT, String.valueOf(PORT_DEFAULT)),
                properties.getProperty(USERNAME),
                properties.getProperty(PASSWORD));
    }
}
