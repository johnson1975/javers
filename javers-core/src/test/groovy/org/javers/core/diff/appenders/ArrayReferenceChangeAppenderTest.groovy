package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.metamodel.object.ValueObjectIdDTO
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class ArrayReferenceChangeAppenderTest extends AbstractDiffAppendersTest {
    def "should append ReferenceChanged in Array of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = arrayChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasReferenceChange(1,instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in Array of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceRemoved in Array of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2)])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in Array of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  arrayOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  arrayOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfValueObjects"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "arrayOfValueObjects/1"))
    }

    def "should append ReferenceRemoved in Array of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  arrayOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  arrayOfValueObjects:[new DummyAddress("London")])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfValueObjects"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "arrayOfValueObjects/1"))
    }

    def "should not append ReferenceChanged in Array of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfValueObjects:[new DummyAddress("London","Street")])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfValueObjects"))

        then:
        !change
    }
}
