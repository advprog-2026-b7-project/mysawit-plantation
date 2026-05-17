package id.ac.ui.cs.advprog.mysawit.plantation.service.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinateNormalizationServiceTest {

    private final CoordinateNormalizationService service =
            new CoordinateNormalizationService();

    @Test
    void normalize_validRectangle_returnsNormalized() {
        List<List<Integer>> coords = List.of(
                List.of(0, 0), List.of(200, 0), List.of(200, 200), List.of(0, 200)
        );
        NormalizedCoordinates result = service.normalize(coords);
        assertEquals(0, result.minX());
        assertEquals(0, result.minY());
        assertEquals(200, result.maxX());
        assertEquals(200, result.maxY());
    }

    @Test
    void normalize_coordinatesOutOfOrder_normalizes() {
        List<List<Integer>> coords = List.of(
                List.of(100, 100), List.of(0, 100), List.of(100, 0), List.of(0, 0)
        );
        NormalizedCoordinates result = service.normalize(coords);
        assertEquals(0, result.minX());
        assertEquals(0, result.minY());
        assertEquals(100, result.maxX());
        assertEquals(100, result.maxY());
    }

    @Test
    void normalize_null_throws() {
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(null)
        );
    }

    @Test
    void normalize_wrongCount_throws() {
        List<List<Integer>> coords = List.of(
                List.of(0, 0), List.of(100, 0), List.of(100, 100)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(coords)
        );
    }

    @Test
    void normalize_notRectangle_throws() {
        // 4 points but only one distinct X — not 2 distinct X values
        List<List<Integer>> coords = List.of(
                List.of(0, 0), List.of(0, 50), List.of(50, 0), List.of(50, 50)
        );
        // This forms a valid rectangle — use a truly non-rectangular set:
        // 3 X values means uniqueX.size() != 2
        List<List<Integer>> nonRect = List.of(
                List.of(0, 0), List.of(100, 0), List.of(200, 100), List.of(0, 100)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(nonRect)
        );
    }

    @Test
    void normalize_zeroWidthRectangle_throws() {
        // All same X — uniqueX.size() == 1
        List<List<Integer>> coords = List.of(
                List.of(50, 0), List.of(50, 100), List.of(50, 200), List.of(50, 300)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(coords)
        );
    }

    @Test
    void normalize_duplicatePoints_throws() {
        // Corners with duplicates — does not match expected 4 distinct corners
        List<List<Integer>> coords = List.of(
                List.of(0, 0), List.of(100, 0), List.of(100, 100), List.of(0, 0)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(coords)
        );
    }

    @Test
    void normalize_invalidPair_throws() {
        // Pair with only 1 element
        List<List<Integer>> coords = Arrays.asList(
                List.of(0, 0), List.of(100), List.of(100, 100), List.of(0, 100)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(coords)
        );
    }

    @Test
    void normalize_nullPair_throws() {
        List<List<Integer>> coords = Arrays.asList(
                List.of(0, 0), null, List.of(100, 100), List.of(0, 100)
        );
        assertThrows(
                ValidationFailedException.class,
                () -> service.normalize(coords)
        );
    }
}
