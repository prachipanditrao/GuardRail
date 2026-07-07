package com.example.demo.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.example.demo", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTests {

    // Rule 1: Structural Layer boundaries (This covers 90% of your isolation checks)
    @ArchTest
    public static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controllers").definedBy("com.example.demo.controller..")
            .layer("Services").definedBy("com.example.demo.service..")
            .layer("Repositories").definedBy("com.example.demo.repository..")
            
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services");

    // Rule 2: Explicitly block Controller -> Repository shortcuts
    @ArchTest
    public static final ArchRule controllers_must_not_access_repositories_directly = noClasses()
            .that().resideInAPackage("com.example.demo.controller..")
            .should().dependOnClassesThat().resideInAPackage("com.example.demo.repository..");

    // Rule 3: Enforce unidirectional flow (No upside-down calls)
    @ArchTest
    public static final ArchRule services_must_not_depend_on_controllers = noClasses()
            .that().resideInAPackage("com.example.demo.service..")
            .should().dependOnClassesThat().resideInAPackage("com.example.demo.controller..");

    // Rule 4: Prevent DTO data models from bleeding backwards into operations
    @ArchTest
    public static final ArchRule dtos_must_remain_isolated = noClasses()
            .that().resideInAPackage("com.example.demo.dto..")
            .should().dependOnClassesThat().resideInAPackage("com.example.demo.service..");
}