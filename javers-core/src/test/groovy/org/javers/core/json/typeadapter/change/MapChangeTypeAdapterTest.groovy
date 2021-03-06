package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.appenders.MapChangeAssert
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.json.JsonConverter
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.JaversTestBuilder.javersTestAssemblyTypeSafe
import static org.javers.core.json.builder.ChangeTestBuilder.mapChange
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class MapChangeTypeAdapterTest extends Specification {

    //@Ignore //not supported
    //def "should deserialize polymorfic MapChange type-safely when switched on" () {
    //    expect:
    //        true
    //}

    def "should serialize polymorfic MapChange type-safely when switched on" () {
        JsonConverter jsonConverter = javersTestAssemblyTypeSafe().jsonConverter
        def entryChanges = [new EntryAdded("some",new LocalDate(2001,1,1)),
                            new EntryRemoved("some",new LocalDate(2002,1,1)),
                            new EntryValueChange(new LocalDate(2003,1,1), new LocalDate(2004,1,1), new LocalDate(2005,1,1))]

        MapChange change = mapChange(new SnapshotEntity(id:1),"polymorficMap",entryChanges)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.entryChanges.size() == 3
        with(json.entryChanges[0]){
            entryChangeType == "EntryAdded"
            key == "some"
            value.typeAlias == "LocalDate"
            value.value == "2001-01-01"
        }
        with(json.entryChanges[1]){
            entryChangeType == "EntryRemoved"
            key == "some"
            value.typeAlias == "LocalDate"
            value.value == "2002-01-01"
        }
        with(json.entryChanges[2]){
            entryChangeType == "EntryValueChange"
            key.typeAlias  == "LocalDate"
            key.value      == "2003-01-01"
            leftValue.typeAlias == "LocalDate"
            leftValue.value     == "2004-01-01"
            rightValue.typeAlias == "LocalDate"
            rightValue.value     == "2005-01-01"
        }
    }

    def "should serialize MapChange.EntryValueChange with Values using custom TypeAdapter" () {
        given:
        def javers = javersTestAssembly()
        JsonConverter jsonConverter = javers.jsonConverter

        def entryChanges = [new EntryValueChange(new LocalDate(2001,1,1),
                                                 1.12,1.13)]
        def change = mapChange(new SnapshotEntity(id:1),"mapOfValues",entryChanges)

        when:
            String jsonText = jsonConverter.toJson(change)

        then:
            def json = new JsonSlurper().parseText(jsonText)
            with(json.entryChanges[0]){
                entryChangeType == "EntryValueChange"
                key == "2001-01-01"
                leftValue == 1.12
                rightValue == 1.13
            }

    }

    def "should deserialize MapChange.EntryValueChange with Values using custom TypeAdapter" () {
        given:
            def javers = javersTestAssembly()
            JsonConverter jsonConverter = javers.jsonConverter

            def json = new JsonBuilder()
            json{
                        changeType "MapChange"
                        globalId {
                            entity "org.javers.core.model.SnapshotEntity"
                            cdoId 1
                        }
                        commitMetadata {
                            author "kazik"
                            commitDate "2001-12-01T22:23:03"
                            commitId "1.0"
                        }
                        property "mapOfValues"
                        entryChanges ([
                                {
                                    entryChangeType "EntryValueChange"
                                    key "2001-01-01"
                                    leftValue 1.12
                                    rightValue 1.13
                                }
                        ])

                }

        when:
            MapChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
            MapChangeAssert.assertThat(change)
                           .hasEntryValueChange(new LocalDate(2001,1,1), 1.12, 1.13)
    }
    def "should serialize MapChange.EntryValueChange with references" () {
        given:
        def javers = javersTestAssembly()
        JsonConverter jsonConverter = javers.jsonConverter

        def keyId  =    javers.idBuilder().instanceId(1,SnapshotEntity)
        def leftReference  = javers.idBuilder().instanceId(2,SnapshotEntity)
        def rightReference = javers.idBuilder().instanceId(3,SnapshotEntity)
        def entryChanges = [new EntryValueChange(keyId, leftReference, rightReference)]

        def change = mapChange(new SnapshotEntity(id:1),"mapOfEntities",entryChanges)

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        with(json.entryChanges[0]){
            entryChangeType == "EntryValueChange"
            key.entity  == "org.javers.core.model.SnapshotEntity"
            key.cdoId   == 1
            leftValue.entity == "org.javers.core.model.SnapshotEntity"
            leftValue.cdoId  == 2
            rightValue.entity == "org.javers.core.model.SnapshotEntity"
            rightValue.cdoId  == 3
        }
    }

    def "should deserialize MapChange.EntryValueChange with references" () {
        given:
            def javers = javersTestAssembly()
            JsonConverter jsonConverter = javers.jsonConverter

            def json = new JsonBuilder()
            json  {
                        changeType "MapChange"
                        globalId {
                            entity "org.javers.core.model.SnapshotEntity"
                            cdoId 1
                        }
                        commitMetadata {
                            author "kazik"
                            commitDate "2001-12-01T22:23:03"
                            id "1.0"
                        }
                        property "mapOfEntities"
                        entryChanges ([
                                {
                                    entryChangeType "EntryValueChange"
                                    key{
                                        entity "org.javers.core.model.SnapshotEntity"
                                        cdoId 2
                                    }
                                    leftValue {
                                        entity "org.javers.core.model.SnapshotEntity"
                                        cdoId 3
                                    }
                                    rightValue {
                                        entity "org.javers.core.model.SnapshotEntity"
                                        cdoId 4
                                    }
                                }
                        ])

                    }

        when:
            //println json.toPrettyString()
            MapChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
            MapChangeAssert.assertThat(change)
                           .hasEntryValueChange(instanceId(2,SnapshotEntity),
                                                instanceId(3,SnapshotEntity),
                                                instanceId(4,SnapshotEntity))
    }

    def "should deserialize MapChange with primitives" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json
                {
                    changeType "MapChange"
                    globalId {
                        entity "org.javers.core.model.SnapshotEntity"
                        cdoId 1
                    }
                    commitMetadata {
                        author "kazik"
                        commitDate "2001-12-01T22:23:03"
                        id "1.0"
                    }
                    property "mapOfPrimitives"
                    entryChanges ([
                            {
                                entryChangeType "EntryAdded"
                                key "some1"
                                value 1
                            },
                            {
                                entryChangeType "EntryRemoved"
                                key "some2"
                                value 2
                            },
                            {
                                entryChangeType "EntryValueChange"
                                key "some3"
                                leftValue 3
                                rightValue 4
                            }
                    ])

                }

        when:
        //println json.toPrettyString()
        MapChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change.affectedGlobalId == instanceId(1,SnapshotEntity)
        change.commitMetadata.get().author == "kazik"
        change.commitMetadata.get().id == "1.0"
        change.commitMetadata.get().commitDate == new LocalDateTime("2001-12-01T22:23:03")
        change.property.name == "mapOfPrimitives"
        MapChangeAssert.assertThat(change)
                       .hasEntryAdded("some1",1)
                       .hasEntryRemoved("some2",2)
                       .hasEntryValueChange("some3",3,4)
    }

    def "should serialize MapChange with primitives" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def entryChanges = [new EntryAdded("some",1),
                            new EntryRemoved("some",2),
                            new EntryValueChange("mod",3,4)]

        MapChange change = mapChange(dummyUser("kaz").build(),"valueMap",entryChanges)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "valueMap"
        json.changeType == "MapChange"
        json.globalId
        json.entryChanges.size() == 3
        with(json.entryChanges[0]){
            entryChangeType == "EntryAdded"
            key == "some"
            value == 1
        }
        with(json.entryChanges[1]){
            entryChangeType == "EntryRemoved"
            key == "some"
            value == 2
        }
        with(json.entryChanges[2]){
            entryChangeType == "EntryValueChange"
            key == "mod"
            leftValue == 3
            rightValue == 4
        }
    }
}
