package no.unit.nva.institution.proxy;

import static nva.commons.utils.attempt.Try.attempt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testutils.HttpClientReturningInfoOfSingleUnits.FIRST_LEVEL_CHILD_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.NON_EXISTING_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.ROOT_NODE_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.SECOND_LEVEL_CHILD_URI;
import static testutils.HttpClientReturningInfoOfSingleUnits.TESTING_LANGUAGE;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.unit.nva.institution.proxy.exception.GatewayException;
import no.unit.nva.institution.proxy.exception.InvalidUriException;
import no.unit.nva.institution.proxy.exception.NonExistingUnitError;
import no.unit.nva.institution.proxy.utils.UriUtils;
import nva.commons.utils.IoUtils;
import nva.commons.utils.attempt.Try;
import org.apache.http.HttpStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import testutils.HttpClientReturningInfoOfSingleUnits;

public class SingleUnitHierarchyGeneratorTest {

    public static final String JSONLD = "JSONLD";
    public static final String EMPTY_BODY = "{}";

    private HttpClient mockHttpClient = new HttpClientReturningInfoOfSingleUnits();

    @DisplayName("SingleUnitHierarchyGenerator returns model with single item when input item has no parents")
    @Test
    public void singleUnitHierarchyGeneratorReturnsModelWithSingleItemWhenInputItemHasNoParents()
        throws InterruptedException, InvalidUriException, NonExistingUnitError, GatewayException {
        SingleUnitHierarchyGenerator generator = new SingleUnitHierarchyGenerator(ROOT_NODE_URI, TESTING_LANGUAGE,
            mockHttpClient);
        String jsonLd = generator.toJsonLd();

        Model model = parseModel(jsonLd);
        assertThatThereIsOnlyExpectedNumberOfUnits(model, 1);
    }

    @DisplayName("SingleUnitHierarchyGenerator returns model with two items when input item is a level one child")
    @Test
    public void singleUnitHierarchyGeneratorReturnsModelWithTwoItemsWhenInputItemIsALevelOneChild()
        throws InterruptedException, InvalidUriException, NonExistingUnitError, GatewayException {

        SingleUnitHierarchyGenerator generator = new SingleUnitHierarchyGenerator(FIRST_LEVEL_CHILD_URI,
            TESTING_LANGUAGE, mockHttpClient);
        String jsonLd = generator.toJsonLd();
        Model model = parseModel(jsonLd);
        assertThatThereIsOnlyExpectedNumberOfUnits(model, 2);
    }

    @DisplayName("SingleUnitHierarchyGenerator returns model with 3 items when input item is a level two child")
    @Test
    public void singleUnitHierarchyGeneratorReturnsModelWithThreeItemsWhenInputItemIsALevelTwoChild()
        throws InterruptedException, InvalidUriException, NonExistingUnitError, GatewayException {

        SingleUnitHierarchyGenerator generator = new SingleUnitHierarchyGenerator(SECOND_LEVEL_CHILD_URI,
            TESTING_LANGUAGE, mockHttpClient);
        String jsonLd = generator.toJsonLd();
        Model model = parseModel(jsonLd);
        assertThatThereIsOnlyExpectedNumberOfUnits(model, 3);
    }

    @DisplayName("SingleUnitHierarchyGenerator returns models with URIs without parameters")
    @Test
    public void singleUnitHierarchyGeneratorReturnsModelsWithUrisWithoutParameters() {
        URI[] input = new URI[]{ROOT_NODE_URI, FIRST_LEVEL_CHILD_URI, SECOND_LEVEL_CHILD_URI};
        List<Try<String>> tryGetModels = getModelsForUris(input);
        assertThatNoFailureHasOccurred(tryGetModels);
        Stream<String> models = tryGetModels.stream().map(Try::get);
        Stream<Resource> subjects = extractAllSubjectsFromAllModels(models);
        assertThatAllSubjectsHaveUrisWithoutParameters(subjects);
    }

    @DisplayName("SingleUnitHierarchyGenerator throws NonExistingUnitError for a valid URI but non existing Unit")
    @Test
    public void singleUnitHierarchyGeneratorThrowsNonExistingUnitErrorForAValidURiButNonExistingUnit()
        throws IOException, InterruptedException {
        HttpClient httpClientReturningNull = mock(HttpClient.class);
        HttpResponse<String> notFound = mock(HttpResponse.class);
        when(notFound.body()).thenReturn(EMPTY_BODY);
        when(notFound.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(httpClientReturningNull.sendAsync(any(HttpRequest.class), any(BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(notFound));
        Executable action =
            () -> new SingleUnitHierarchyGenerator(NON_EXISTING_URI, TESTING_LANGUAGE, httpClientReturningNull);
        NonExistingUnitError error = assertThrows(NonExistingUnitError.class, action);
        assertThat(error.getStatusCode(), is(equalTo(HttpStatus.SC_BAD_GATEWAY)));
    }

    private void assertThatAllSubjectsHaveUrisWithoutParameters(Stream<Resource> subjects) {
        subjects.map(Resource::getURI)
                .map(URI::create)
                .forEach(
                    uri -> assertThat(uri, is(equalTo(clearParameters(uri)))));
    }

    private Stream<Resource> extractAllSubjectsFromAllModels(Stream<String> models) {
        return models.map(this::parseModel)
                     .flatMap(model -> model.listSubjects().toList().stream());
    }

    private List<Try<String>> getModelsForUris(URI[] input) {
        return Arrays.stream(input)
                     .map(attempt(uri -> new SingleUnitHierarchyGenerator(uri, TESTING_LANGUAGE, mockHttpClient)))
                     .map(eff -> eff.map(SingleUnitHierarchyGenerator::toJsonLd))
                     .collect(Collectors.toList());
    }

    private void assertThatNoFailureHasOccurred(List<Try<String>> tryGetModels) {
        tryGetModels.forEach(eff -> assertTrue(eff.isSuccess()));
    }

    private URI clearParameters(URI uri) throws IllegalStateException {
        try {
            return UriUtils.clearParameters(uri);
        } catch (InvalidUriException e) {
            throw new IllegalStateException(e);
        }
    }

    private void assertThatThereIsOnlyExpectedNumberOfUnits(Model model, int expectedNumberOfUnits) {
        Set<Resource> subjects = model.listSubjects().toSet();
        assertThat(subjects.size(), is(equalTo(expectedNumberOfUnits)));
    }

    private Model parseModel(String jsonLd) {
        Model model = ModelFactory.createDefaultModel();
        model.read(IoUtils.stringToStream(jsonLd), null, JSONLD);
        return model;
    }
}