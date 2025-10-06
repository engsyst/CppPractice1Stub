package ua.nure.cpp.name.practice1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplianceTest {
    private static final Logger LOG = LoggerFactory.getLogger(ComplianceTest.class);

    static final String BASE_PACKAGE = "practice1";
    static final List<String> TASK_CLASSES = List.of("Part1", "Part2", "Demo");
    static final List<String> FORBIDDEN_PACKAGES = List.of("java.util");
    static final List<String> FORBIDDEN_EXCEPTIONS = List.of();
    static CtModel ctModel = getCtModel();

    static synchronized CtModel getCtModel() {
        if (ctModel == null) {
            SpoonAPI spoon = new Launcher();2
            spoon.addInputResource("src/main/java/");
            ctModel = spoon.buildModel();
        }
        return ctModel;
    }

    static String getBasePackage() {
        Optional<String> any = ctModel.getAllPackages().stream()
                .map(CtPackage::getQualifiedName)
                .filter(pfqn -> pfqn.endsWith(BASE_PACKAGE))
                .findAny();
        assertTrue(any.isPresent());
        return any.get();
    }

    public static Stream<Arguments> sourceComplianceForbiddenPackages() {
        return FORBIDDEN_PACKAGES.stream()
                .map(p -> Arguments.of(p, FORBIDDEN_EXCEPTIONS.toArray(new String[0])));
    }

    @Test
    void testComplianceTopLevelClass() {
        String basePackage = getBasePackage();
        List<String> allClasses = ctModel
                .filterChildren((Filter<CtType<?>>) element -> element.getQualifiedName()
                        .startsWith(basePackage))
                .list()
                .stream()
                .map(c ->((CtType<?>) c).getQualifiedName())
                .toList();
        assertTrue(allClasses.containsAll(TASK_CLASSES.stream().map(c -> basePackage + "." + c).toList()),
                "All task classes must be implemented");
    }

    @Test
    void testCompliancePackageNaming() {

        String basePackage = getBasePackage();
        Matcher m = Pattern.compile("ua\\.nure\\.cpp\\.[a-z&&\\D]+?\\." + BASE_PACKAGE).matcher(basePackage);
        assertTrue(m.matches(),
                "Base package must be 'ua.nure.cpp.<your last name>." + BASE_PACKAGE + "'.\n" +
                        "Where <your last name> is your last name in lower case without digits.");
        assertNotEquals("ua.nure.cpp.name." + BASE_PACKAGE, basePackage,
                "Base package must be 'ua.nure.cpp.<your last name>." + BASE_PACKAGE + "'.\n" +
                        "Where <your last name> is your last name in lower case without digits.");
        assertNotEquals("ua.nure.cpp.your_last_name." + BASE_PACKAGE, basePackage,
                "Base package must be 'ua.nure.cpp.<your last name>." + BASE_PACKAGE + "'.\n" +
                        "Where <your last name> is your last name in lower case without digits.");
    }

    // java.util is prohibited
    @ParameterizedTest()
    @MethodSource("sourceComplianceForbiddenPackages")
    void testComplianceForbiddenPackages(String forbiddenPackage, String[] allowed) {
        List<String> forbiddenClasses = ctModel
                .filterChildren((Filter<CtTypeReference<?>>) el ->
                        el.getQualifiedName().startsWith(forbiddenPackage))
                .list()
                .stream()
                .map(el -> ((CtTypeReference<?>) el).getQualifiedName())
                .distinct()
                .filter(el -> el.startsWith(forbiddenPackage) &&
                        FORBIDDEN_EXCEPTIONS.stream().noneMatch(el::endsWith)
                ).toList();
        LOG.debug("forbiddenClasses: {}", forbiddenClasses);
        assertTrue(forbiddenClasses.isEmpty(), FORBIDDEN_PACKAGES + " package(s) is forbidden " +
                "except " + FORBIDDEN_EXCEPTIONS +
                "Your forbidden classes: " + forbiddenClasses);
    }
}
