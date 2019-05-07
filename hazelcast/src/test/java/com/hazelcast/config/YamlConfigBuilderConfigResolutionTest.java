/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.config;

import com.hazelcast.config.helpers.DeclarativeConfigFileHelper;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;

import static com.hazelcast.test.HazelcastTestSupport.assumeThatJDK8OrHigher;
import static org.junit.Assert.assertEquals;

@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class YamlConfigBuilderConfigResolutionTest {

    private static final String SYSPROP_NAME = "hazelcast.config";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DeclarativeConfigFileHelper helper = new DeclarativeConfigFileHelper();

    @Before
    public void setUp() {
        assumeThatJDK8OrHigher();
        System.clearProperty(SYSPROP_NAME);
    }

    @After
    public void tearDown() {
        System.clearProperty(SYSPROP_NAME);
        helper.ensureTestConfigDeleted();
    }

    @Test
    public void testResolveSystemProperty_file_yaml() throws Exception {
        helper.givenYamlConfigFileInWorkDir("foo.yaml", "cluster-yaml-file");
        System.setProperty(SYSPROP_NAME, "foo.yaml");

        Config config = new YamlConfigBuilder().build();
        assertEquals("cluster-yaml-file", config.getInstanceName());
    }

    @Test
    public void testResolveSystemProperty_classpath_yaml() throws Exception {
        helper.givenYamlConfigFileOnClasspath("foo.yaml", "cluster-yaml-classpath");
        System.setProperty(SYSPROP_NAME, "classpath:foo.yaml");

        Config config = new YamlConfigBuilder().build();
        assertEquals("cluster-yaml-classpath", config.getInstanceName());
    }

    @Test
    public void testResolveSystemProperty_classpath_nonExistentYaml_throws() {
        System.setProperty(SYSPROP_NAME, "classpath:idontexist.yaml");

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("classpath");
        expectedException.expectMessage("idontexist.yaml");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveSystemProperty_file_nonExistentYaml_throws() {
        System.setProperty(SYSPROP_NAME, "idontexist.yaml");

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("idontexist.yaml");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveSystemProperty_file_nonYaml_throws() throws Exception {
        File file = helper.givenYamlConfigFileInWorkDir("foo.bar", "irrelevant");
        System.setProperty(SYSPROP_NAME, file.getAbsolutePath());

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("suffix");
        expectedException.expectMessage("foo.bar");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveSystemProperty_classpath_nonYaml_throws() throws Exception {
        helper.givenYamlConfigFileOnClasspath("foo.bar", "irrelevant");
        System.setProperty(SYSPROP_NAME, "classpath:foo.bar");

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("suffix");
        expectedException.expectMessage("foo.bar");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveSystemProperty_file_nonExistentNonYaml_throws() {
        System.setProperty(SYSPROP_NAME, "foo.bar");

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("foo.bar");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveSystemProperty_classpath_nonExistentNonYaml_throws() {
        System.setProperty(SYSPROP_NAME, "classpath:idontexist.bar");

        expectedException.expect(HazelcastException.class);
        expectedException.expectMessage("classpath");
        expectedException.expectMessage("idontexist.bar");

        new YamlConfigBuilder().build();
    }

    @Test
    public void testResolveFromWorkDir() throws Exception {
        helper.givenYamlConfigFileInWorkDir("cluster-yaml-workdir");

        Config config = new YamlConfigBuilder().build();

        assertEquals("cluster-yaml-workdir", config.getInstanceName());
    }

    @Test
    public void testResolveFromClasspath() throws Exception {
        helper.givenYamlConfigFileOnClasspath("cluster-yaml-classpath");

        Config config = new YamlConfigBuilder().build();

        assertEquals("cluster-yaml-classpath", config.getInstanceName());
    }

    @Test
    public void testResolveDefault() {
        Config config = new YamlConfigBuilder().build();
        assertEquals("dev", config.getGroupConfig().getName());
    }

}
