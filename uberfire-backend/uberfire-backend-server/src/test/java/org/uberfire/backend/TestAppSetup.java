/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.backend;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.metadata.backend.lucene.LuceneIndexEngine;
import org.uberfire.metadata.backend.lucene.LuceneSetup;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.uberfire.metadata.backend.lucene.setups.RAMLuceneSetup;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.engine.MetaModelStore;
import org.uberfire.metadata.io.IOServiceIndexedImpl;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;

@Singleton
@Alternative
public class TestAppSetup {

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    private final LuceneSetup luceneSetup = new RAMLuceneSetup();

    private SessionInfo sessionInfo = new SessionInfoImpl();

    private IOService ioService;

    @PostConstruct
    public void init() {
        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore,
                                                                   luceneSetup,
                                                                   new SimpleFieldFactory() );

        ioService = new IOServiceIndexedImpl( watchService,
                                              indexEngine,
                                              DublinCoreView.class,
                                              VersionAttributeView.class );

    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    public SessionInfo getSessionInfo() {
        return this.sessionInfo;
    }

}
