Feature: Get single institution

  Scenario Outline: The user downloads a single institution in the default language
    Given that the user has a valid <Institution IRI>
    When they set the Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is the nested structure for the entire institution
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Institution IRI                            |
      | https://api.cristin.no/v2/institutions/185 |
      | https://api.cristin.no/v2/institutions/194 |

  Scenario Outline: The user downloads a single institution in Norwegian Bokmål
    Given that the user has a valid <Institution IRI>
    When they set the Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>&language=nb
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is the nested structure for the entire institution
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Institution IRI                            |
      | https://api.cristin.no/v2/institutions/185 |
      | https://api.cristin.no/v2/institutions/194 |

  Scenario Outline: The user downloads a single institution in Norwegian Nynorsk
    Given that the user has a valid <Institution IRI>
    When they set the Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>&language=nn
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is the nested structure for the institution
    And they see that the texts in the response body are mostly in Norwegian Nynorsk

    Examples:
      | Institution IRI                            |
      | https://api.cristin.no/v2/institutions/185 |
      | https://api.cristin.no/v2/institutions/194 |

  Scenario Outline: The user downloads a single institution in English
    Given that the user has a valid <Institution IRI>
    When they set the Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>&language=en
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is the nested structure for the institution
    And they see that the texts in the response body are mostly in English

    Examples:
      | Institution IRI                            |
      | https://api.cristin.no/v2/institutions/185 |
      | https://api.cristin.no/v2/institutions/194 |

  Scenario Outline: The user requests an institution that does not exist
    Given that the user has an invalid <institution IRI>
    When they set the request Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>
    Then they receive an response with status code 404
    And they see that the response Content-type is "application/problem+json"
    And they see the response body contains a problem.json object
    And they see the response body has a field "title" with the value "Not found"
    And they see the response body has a field "status" with the value "404"
    Examples:
      | Institution IRI       |
      | https://example.org/1 |
      | https://example.org/2 |

  Scenario Outline: The third party service is unavailable
    Given that the third party service is unavailable
    When they set the Accept header to "application/json"
    And they request GET /institution?iri=<Institution IRI>
    Then they receive a response with status code 502
    And they see that the response Content-type is "application/problem+json"
    And they see the response body contains a problem.json object
    And they see the response body has a field "title" with the value "Bad gateway"
    And they see the response body has a field "status" with the value "502"
    And they see the response body has a field "detail" with the value "The institution service is unavailable"

    Examples:
      | Institution IRI                            |
      | https://api.cristin.no/v2/institutions/185 |
      | https://api.cristin.no/v2/institutions/194 |

  Scenario: User requests a badly formatted IRI
    Given that the user has a badly formatted Institution IRI
    When they set the request Accept header to "application/json"
    And they request GET /institution?iri=notaniri
    Then they receive an response with status code 400
    And they see that the response Content-type is "application/problem+json"
    And they see the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad request"
    And they see the response body has a field "status" with the value "400"
    And they see the response body has a field "detail" with the value "The request contained a malformed IRI"

  Scenario: User creates a request without the required query parameters
    When the user sets the request Accept header to "application/json"
    And they request GET /institution?not_ok=https://api.cristin.no/v2/institutions/194
    Then they receive an response with status code 400
    And they see that the response Content-type is "application/problem+json"
    And they see the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad request"
    And they see the response body has a field "status" with the value "400"
    And they see the response body has a field "detail" with the value "The request did not contain required parameter \"iri\""

  Scenario: User creates a request with unrecognized query parameters
    When the user sets the request Accept header to "application/json"
    And they request GET /institution?iri=https://api.cristin.no/v2/institutions/194&lang=nn
    Then they receive an response with status code 400
    And they see that the response Content-type is "application/problem+json"
    And they see the response body is a problem.json object
    And they see the response body has a field "title" with the value "Bad request"
    And they see the response body has a field "status" with the value "400"
    And they see the response body has a field "detail" with the value:
      """
      The request contained unrecognized parameter \"lang\", valid parameters include \"iri\" and \"language\"
      """