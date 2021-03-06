package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotFactory;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Decomposes given live object graph into a flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 *
 * @author bartosz walacik
 */
class GraphSnapshotFactory {

    private final SnapshotFactory snapshotFactory;
    private final JaversExtendedRepository javersRepository;

    GraphSnapshotFactory(SnapshotFactory snapshotFactory, JaversExtendedRepository javersRepository) {
        this.snapshotFactory = snapshotFactory;
        this.javersRepository = javersRepository;
    }

    /**
     * @param currentVersion outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    List<CdoSnapshot> create(LiveGraph currentVersion, ShadowGraph latestShadowGraph, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(currentVersion, commitMetadata, latestShadowGraph);

        List<CdoSnapshot> reused = new ArrayList<>();

        for (ObjectNode node : currentVersion.nodes()) {
            boolean initial = isInitial(node, latestShadowGraph);

            CdoSnapshot fresh;
            if (initial){
                fresh = snapshotFactory.createInitial(node, commitMetadata);
            }
            else{
                fresh = snapshotFactory.create(node, commitMetadata);
            }

            Optional<CdoSnapshot> existing = javersRepository.getLatest(fresh.getGlobalId());
            if (existing.isEmpty()) {
                reused.add(fresh);
                continue;
            }

            if (!existing.get().stateEquals(fresh)) {
                reused.add(fresh);
            }
        }

        return reused;
    }

    List<CdoSnapshot> create(LiveGraph currentVersion, CommitMetadata commitMetadata) {
        return create(currentVersion, ShadowGraph.EMPTY, commitMetadata);
    }

    private boolean isInitial(ObjectNode node, ShadowGraph latestShadowGraph){
        return !latestShadowGraph.nodes().contains(node);
    }
}
