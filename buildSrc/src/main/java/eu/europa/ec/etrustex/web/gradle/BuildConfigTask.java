package eu.europa.ec.etrustex.web.gradle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class BuildConfigTask extends DefaultTask {
    String inputFile;
    String featuresEnumOutputDir;
    private String packageName = "eu.europa.ec.etrustex.web.common.features";
    private String featuresEnumName = "FeaturesEnum";
    private String environmentEnumName = "Environment";
    private static final String DESCRIPTION_FIELD_NAME = "description";

    @TaskAction
    void buildConfig() {
        try {
            Map<String, Object> yamlFileMap = parseYamlFile();

            List<Map<String, Object>> featuresList = getFeaturesListMap(yamlFileMap);

            writeFeaturesJavaEnum(featuresList);
        } catch (Exception e) {
            throw new TaskExecutionException(this, e);
        }
    }

    private Map<String, Object> parseYamlFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        return mapper.readValue(new File(inputFile), typeRef);
    }

    private List<Map<String, Object>> getFeaturesListMap(Map<String, Object> yamlFileMap) {
        Map<String, Object> features = (Map<String, Object>) yamlFileMap.get("features");

        return (List<Map<String, Object>>) features.get("features");
    }

    private void writeFeaturesJavaEnum(List<Map<String, Object>> featuresList) throws IOException {
        TypeSpec.Builder featuresTypeSpecBuilder = featuresTypeSpecBuilder();

        addFeatureEnumConstants(featuresTypeSpecBuilder, featuresList);

        addFieldsAndConstructor(featuresTypeSpecBuilder);

        featuresTypeSpecBuilder.addMethod(MethodSpec.methodBuilder("isActive")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "env")
                .returns(boolean.class)
                .addStatement("return this.envMap.get(" + environmentEnumName + ".valueOf($N.toUpperCase()))", "env")
                .build());

        writeJavaFile(featuresTypeSpecBuilder);
    }

    private TypeSpec.Builder featuresTypeSpecBuilder() {
        TypeSpec envEnum = environmentEnumType();

        TypeSpec.Builder featuresTypeSpecBuilder = TypeSpec.enumBuilder(featuresEnumName);
        featuresTypeSpecBuilder.addModifiers(Modifier.PUBLIC);
        featuresTypeSpecBuilder.addType(envEnum);

        return featuresTypeSpecBuilder;
    }

    private TypeSpec environmentEnumType() {
        TypeSpec.Builder envTypeSpecBuilder = TypeSpec.enumBuilder(environmentEnumName);
        envTypeSpecBuilder.addModifiers(Modifier.PUBLIC);

        return envTypeSpecBuilder.addEnumConstant("DEV")
                .addEnumConstant("TEST")
                .addEnumConstant("ACC")
                .addEnumConstant("PROD")
                .addEnumConstant("ACC_BHS")
                .build();
    }

    @SuppressWarnings({"java:S1192"})
    private void addFieldsAndConstructor(TypeSpec.Builder featuresTypeSpecBuilder) {
        FieldSpec id = FieldSpec.builder(String.class, "id").addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();
        FieldSpec description = FieldSpec.builder(String.class, DESCRIPTION_FIELD_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();

        ParameterizedTypeName envMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get("", environmentEnumName),
                ClassName.get(Boolean.class));

        FieldSpec envMap = FieldSpec.builder(envMapTypeName, "envMap")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer("new $T<>(" + environmentEnumName + ".class)", EnumMap.class)
                .build();

        featuresTypeSpecBuilder
                .addField(id).addField(description).addField(envMap)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "id")
                        .addParameter(String.class, DESCRIPTION_FIELD_NAME)
                        .addParameter(boolean.class, "dev")
                        .addParameter(boolean.class, "test")
                        .addParameter(boolean.class, "acc")
                        .addParameter(boolean.class, "prod")
                        .addParameter(boolean.class, "accBhs")
                        .addStatement("this.$N = $N", "id", "id")
                        .addStatement("this.$N = $N", DESCRIPTION_FIELD_NAME, DESCRIPTION_FIELD_NAME)
                        .addStatement("this.envMap.put(" + environmentEnumName + ".DEV, $N)", "dev")
                        .addStatement("this.envMap.put(" + environmentEnumName + ".TEST, $N)", "test")
                        .addStatement("this.envMap.put(" + environmentEnumName + ".ACC, $N)", "acc")
                        .addStatement("this.envMap.put(" + environmentEnumName + ".PROD, $N)", "prod")
                        .addStatement("this.envMap.put(" + environmentEnumName + ".ACC_BHS, $N)", "accBhs")
                        .build());
    }

    private void addFeatureEnumConstants(TypeSpec.Builder featuresTypeSpecBuilder, List<Map<String, Object>> featuresList) {
        for (Map<String, Object> feature : featuresList) {
            featuresTypeSpecBuilder.addEnumConstant((String) feature.get("id"),
                    TypeSpec.anonymousClassBuilder("$S, $S, $L, $L, $L, $L, $L",
                            feature.get("id"),
                            feature.get(DESCRIPTION_FIELD_NAME),
                            feature.get("dev"),
                            feature.get("test"),
                            feature.get("acc"),
                            feature.get("prod"),
                            feature.get("acc_bhs"))
                            .build());
        }
    }

    private void writeJavaFile(TypeSpec.Builder featuresTypeSpecBuilder) throws IOException {
        TypeSpec featuresTypeSpec = featuresTypeSpecBuilder.build();
        JavaFile javaFile = JavaFile.builder(packageName, featuresTypeSpec).build();
        javaFile.writeTo(new File(featuresEnumOutputDir));
    }
}
