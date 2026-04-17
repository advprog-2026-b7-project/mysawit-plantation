package id.ac.ui.cs.advprog.mysawit.plantation.service.validation;

import java.util.List;

public record NormalizedCoordinates(
        int minX,
        int minY,
        int maxX,
        int maxY,
        List<List<Integer>> points
) {
}
