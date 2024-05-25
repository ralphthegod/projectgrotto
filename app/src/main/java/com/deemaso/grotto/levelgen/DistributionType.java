package com.deemaso.grotto.levelgen;

public enum DistributionType {
    CLUSTERED, // Clustered means that the element is placed in a cluster
    SPARSE, // Sparse means that the element is placed in a random location
    INTERNAL, // Internal means that the element is placed inside the room (near the center)
    EXTERNAL // External means that the element is placed on the edge of the room
}
