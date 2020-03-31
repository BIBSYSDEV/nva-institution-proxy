Feature: Get all institutions

  Scenario: User requests all institutions in the default language
    Given that the API user is looking for a list of institutions in <language>
    When they set the request Accept header to "application/json"
    And they request /institutions
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the response body contains a list of objects
    And they see that each object is an institution of Norwegian higher education that is a Cristin user institution
    And they see that each institution has a IRI
    And they see that each institution has a name
    And they see that the majority of the names are in Norwegian Bokmål

  Scenario Outline: User requests all institutions in a given language
    Given that the API user is looking for a list of institutions in <language>
    When they set the Accept header to application/json
    And they request /institutions?language=<language>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the response body contains a list of objects
    And they see that each object is an institution of Norwegian higher education that is a Cristin user institution    And they see that each institution has a IRI
    And they see that each institution has a name
    And they see that the majority of the names are in the chosen language

    Examples:
      | language |
      | en       |
      | nb       |
      | nn       |

  Scenario: The third party service is unavailable
    Given that the third party service is unavailable
    When they set the Accept header to application/json
    And they request /institutions
    Then they receive a response with status code 502
    And they see the response Content-type is "application/problem+json"
    And they see the response body contains JSON object
    And they see the response body has a field "title" with the value "Bad gateway"
    And they see the response body has a field "status" with the value "502"
    And they see the response body has a field "detail" with the value "The institution service is unavailable"

  Scenario: User creates a request with unrecognized query parameters
    When the user sets the request Accept header to application/json
    And they request /institutions?lang=nn
    Then they receive an response with status code 400
    And they see the response Content-type is "application/problem+json"
    And they see the response body contains JSON object    And the response body has a field "title" with the value "Bad request"
    And they see the response body has a field "status" with the value "400"
    And they see the response body has a field "detail" with the value:
      """
      The request contained unrecognized parameter \"lang\", valid parameters include \"language\"
      """