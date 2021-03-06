package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.map.EntryAddOrRemove
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.model.DummyUser
import org.javers.core.metamodel.property.Property
import org.javers.core.graph.ObjectNode
import org.joda.time.LocalDateTime
import spock.lang.Unroll
import static org.javers.core.diff.ChangeAssert.*

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class MapChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should not append mapChanges when maps are #what" () {
        given:
        def left =  dummyUser("1").withPrimitiveMap(leftMap).build()
        def right = dummyUser("1").withPrimitiveMap(rightMap).build()
        Property valueMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def change = mapChangeAppender().calculateChanges(realNodePair(left,right),valueMap)
        change == null

        where:
        what << ["equal","null"]
        leftMap <<  [["some":1], null]
        rightMap << [["some":1], null]
    }

    def "should set MapChange metadata"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(null).build()
        def right = dummyUser("1").withPrimitiveMap(["some":1]).build()
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def change =  mapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)

        then:
        assertThat(change)
                    .hasProperty(primitiveMap)
                    .hasInstanceId(DummyUser, "1")
    }

    @Unroll
    def "should append #changeType.simpleName when left map is #leftMap and rightMap is #rightMap"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(leftMap).build()
        def right = dummyUser("1").withPrimitiveMap(rightMap).build()
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def change = mapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)
        EntryAddOrRemove entryAddOrRemove = change.entryChanges[0]
        entryAddOrRemove.key == "some"
        entryAddOrRemove.value == 1
        entryAddOrRemove.class == changeType

        where:
        changeType << [EntryAdded,  EntryRemoved,  EntryAdded,           EntryRemoved]
        leftMap <<    [null,        ["some":1],    ["other":1],          ["some":1,"other":1] ]
        rightMap <<   [["some":1],   null,         ["other":1,"some":1], ["other":1] ]
    }

    def "should append EntryValueChanged when Primitive value is changed"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(["some":1,"other":2] ).build()
        def right = dummyUser("1").withPrimitiveMap(["some":2,"other":2]).build()
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def change =  mapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)

        then:
        EntryValueChange entryValueChanged = change.entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue == 1
        entryValueChanged.rightValue == 2
    }

    def "should append EntryValueChanged when ValueType value is changed"() {

        def dayOne = new LocalDateTime(2000,1,1,12,1)
        def dayTwo = new LocalDateTime(2000,1,1,12,2)

        given:
        def left =  dummyUser("1").withValueMap(["some":dayOne, "other":dayTwo] ).build()
        def right = dummyUser("1").withValueMap(["some":dayTwo, "other":dayTwo]).build()
        Property valueMap = getEntity(DummyUser).getProperty("valueMap")

        when:
        def change = mapChangeAppender().calculateChanges(realNodePair(left,right),valueMap)

        then:
        EntryValueChange entryValueChanged = change.entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue ==   dayOne
        entryValueChanged.rightValue ==  dayTwo
    }
}
