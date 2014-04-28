package org.javers.core.snapshot;

import org.javers.core.diff.ObjectGraph;
import org.javers.core.graph.ObjectNode;

import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ShadowGraph implements ObjectGraph {
    private final Set<ObjectNode> snapshots;

    ShadowGraph(Set<ObjectNode> snapshots) {
        this.snapshots = snapshots;
    }

    @Override
    public Set<ObjectNode> flatten() {
        return Collections.unmodifiableSet(snapshots);
    }
}