package no.unit.nva.institution.proxy.utils;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.writer.JsonLDWriter;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.vocabulary.RDF;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;

import static org.apache.jena.riot.RDFFormat.JSONLD_FRAME_PRETTY;

public class ModelUtils {

    public static final Property SUBUNITS_PROPERTY = ResourceFactory
            .createProperty("https://nva.unit.no/ontology#subunits");
    public static final Property NAME_PROPERTY = ResourceFactory
            .createProperty("https://nva.unit.no/ontology#name");
    public static final Resource INSTITUTION_CLASS = ResourceFactory
            .createResource("https://nva.unit.no/ontology#Institution");
    public static final String JSON_LD_FRAME = "src/main/resources/frame.jsonld";
    public static final PrefixMap UNIT_PREFIX_MAP = PrefixMapFactory
            .create(Collections.singletonMap("unit", "https://nva.unit.no/ontology#"));

    private Model model;

    public ModelUtils() {
        this.model = ModelFactory.createDefaultModel();
    }

    public void addTypeToModel(URI uri) {
        model.add(createTypeStatement(uri));
    }

    private Statement createTypeStatement(URI uri) {
        Resource subject = ResourceFactory.createResource(uri.toString());
        return model.createStatement(subject, RDF.type, INSTITUTION_CLASS);
    }

    public void addNameToModel(URI uri, String name) {
        model.add(createNameStatement(uri, name));
    }

    public void addSubunitsRelationToModel(URI parent, URI child) {
        model.add(createSubunitsStatement(parent, child));
    }

    private Statement createNameStatement(URI uri, String name) {
        Resource subject = ResourceFactory.createResource(uri.toString());
        Literal object = ResourceFactory.createPlainLiteral(name);
        return model.createStatement(subject, NAME_PROPERTY, object);
    }

    private Statement createSubunitsStatement(URI uri, URI name) {
        Resource subject = ResourceFactory.createResource(uri.toString());
        Resource object = ResourceFactory.createResource(name.toString());
        return model.createStatement(subject, SUBUNITS_PROPERTY, object);
    }

    public String toJsonLd() throws IOException {
        StringWriter stringWriter = new StringWriter();
        DatasetGraph dataset = DatasetFactory.create(model).asDatasetGraph();
        new JsonLDWriter(JSONLD_FRAME_PRETTY).write(stringWriter, dataset, UNIT_PREFIX_MAP, null,
                getJsonLDWriteContext());
        return stringWriter.toString();
    }

    private JsonLDWriteContext getJsonLDWriteContext() throws IOException {
        JsonLDWriteContext context = new JsonLDWriteContext();
        context.setFrame(getFrame());
        context.setOptions(getJsonLdOptions());
        return context;
    }

    private JsonLdOptions getJsonLdOptions() {
        JsonLdOptions options = new JsonLdOptions();
        options.setOmitGraph(true);
        options.setOmitDefault(true);
        options.setEmbed(true);
        options.setFrameExpansion(false);
        options.setPruneBlankNodeIdentifiers(true);
        return options;
    }

    private Object getFrame() throws IOException {
        return JsonUtils.fromInputStream(new FileInputStream(JSON_LD_FRAME));
    }
}