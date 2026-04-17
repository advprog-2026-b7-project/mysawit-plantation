package id.ac.ui.cs.advprog.mysawit.plantation.service.validation;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CoordinateNormalizationService {

    public NormalizedCoordinates normalize(List<List<Integer>> coordinates) {
        validateCoordinatesShape(coordinates);

        Set<Integer> uniqueX = new HashSet<>();
        Set<Integer> uniqueY = new HashSet<>();
        Set<String> points = new HashSet<>();

        for (List<Integer> pair : coordinates) {
            Integer x = pair.get(0);
            Integer y = pair.get(1);
            uniqueX.add(x);
            uniqueY.add(y);
            points.add(x + "," + y);
        }

        if (uniqueX.size() != 2 || uniqueY.size() != 2) {
            throw new ValidationFailedException("coordinates must form an axis-aligned rectangle");
        }

        int minX = uniqueX.stream().min(Integer::compareTo).orElseThrow();
        int maxX = uniqueX.stream().max(Integer::compareTo).orElseThrow();
        int minY = uniqueY.stream().min(Integer::compareTo).orElseThrow();
        int maxY = uniqueY.stream().max(Integer::compareTo).orElseThrow();

        if (minX == maxX || minY == maxY) {
            throw new ValidationFailedException("coordinates must form a non-zero rectangle");
        }

        Set<String> expected = Set.of(
                minX + "," + minY,
                maxX + "," + minY,
                maxX + "," + maxY,
                minX + "," + maxY
        );

        if (!points.equals(expected)) {
            throw new ValidationFailedException(
                    "coordinates must contain exactly 4 rectangle corners"
            );
        }

        List<List<Integer>> normalized = List.of(
                List.of(minX, minY),
                List.of(maxX, minY),
                List.of(maxX, maxY),
                List.of(minX, maxY)
        );

        return new NormalizedCoordinates(minX, minY, maxX, maxY, normalized);
    }

    private void validateCoordinatesShape(List<List<Integer>> coordinates) {
        if (coordinates == null || coordinates.size() != 4) {
            throw new ValidationFailedException("coordinates must contain exactly 4 pairs [x, y]");
        }
        for (List<Integer> pair : coordinates) {
            if (pair == null || pair.size() != 2 || pair.get(0) == null || pair.get(1) == null) {
                throw new ValidationFailedException(
                        "each coordinate must be a pair of integers [x, y]"
                );
            }
        }
    }
}
