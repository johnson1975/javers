package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.examples.model.Employee;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.junit.Test;

import java.util.List;

public class ChangeLogExample {

    @Test
    public void shoudPrintTextChangeLog() {
        // given:
        Javers javers = JaversBuilder.javers().build();
        Employee bob = new Employee("Bob", 9_000, "Scrum master" );
        javers.commit("hr.manager", bob);

        // do some changes and commit
        bob.setPosition("Team Lead");
        bob.setSalary(11_000);
        javers.commit("hr.director", bob);

        bob.addSubordinates(new Employee("Trainee One"), new Employee("Trainee Two"));
        javers.commit("hr.manager", bob);

        // when:
        List<Change> changes = javers.getChangeHistory(InstanceIdDTO.instanceId("Bob", Employee.class),5);
        String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());

        // then:
        System.out.println(changeLog);
    }
}
