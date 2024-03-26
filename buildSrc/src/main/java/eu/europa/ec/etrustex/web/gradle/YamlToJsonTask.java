package eu.europa.ec.etrustex.web.gradle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;


public class YamlToJsonTask extends DefaultTask {
    String inputYmlFile;
    String outputJsonFile;

    @TaskAction
    void buildConfig() {
        try {
            Map<String, Object> yamlFileMap = parseYamlFile();
            writeJsonFile(yamlFileMap);
        } catch (Exception e) {
            throw new TaskExecutionException(this, e);
        }
    }

    private Map<String, Object> parseYamlFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        return mapper.readValue(new File(inputYmlFile), typeRef);
    }

    private void writeJsonFile(Map<String, Object> yamlFileMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(outputJsonFile).toFile(), yamlFileMap);
    }
}
