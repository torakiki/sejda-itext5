/*
 * Created on 12/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl;

import org.junit.Ignore;
import org.sejda.model.task.BaseTask;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public class TestTask extends BaseTask<TestTaskParameter> {

    public void after() {
        // nothing

    }

    public void before(TestTaskParameter parameters) {
        // nothing
    }

    public void execute(TestTaskParameter parameters) {
        // nothing
    }

}
