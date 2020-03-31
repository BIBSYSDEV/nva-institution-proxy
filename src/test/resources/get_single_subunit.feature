Feature: Get single subunit
  
  Scenario Outline: The user downloads a single subunit in the default language
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                   |
      | https://api.cristin.no/v2/units/194.31.0.0 |

  Scenario Outline: The user downloads a single subsubunit in Norwegian Bokmål
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>?language=nb
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                   |
      | https://api.cristin.no/v2/units/194.31.0.0 |

  Scenario Outline: The user downloads a single subsubunit in Norwegian Nynorsk
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=nn
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Nynorsk
    Examples:
      | Unit IRI                                   |
      | https://api.cristin.no/v2/units/194.31.0.0 |

  Scenario Outline: The user downloads a single subsubunit in English
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=en
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in English

    Examples:
      | Unit IRI                                   |
      | https://api.cristin.no/v2/units/194.31.0.0 |

  Scenario Outline: The user downloads a single subsubunit in the default language
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                    |
      | https://api.cristin.no/v2/units/194.31.15.0 |

  Scenario Outline: The user downloads a single subsubunit in Norwegian Bokmål
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>?language=nb
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                    |
      | https://api.cristin.no/v2/units/194.31.15.0 |

  Scenario Outline: The user downloads a single subsubunit in Norwegian Nynorsk
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=nn
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Nynorsk
    Examples:
      | Unit IRI                                    |
      | https://api.cristin.no/v2/units/194.31.15.0 |

  Scenario Outline: The user downloads a single subsubunit in English
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=en
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that contains the <Unit IRI>
    And they see that the texts in the response body are mostly in English

    Examples:
      | Unit IRI                                    |
      | https://api.cristin.no/v2/units/194.31.15.0 |

  Scenario Outline: The user downloads a single subsubsubunit in the default language
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that has a single subsubsubunit
    And they see the subsubsubunit is a single object contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                     |
      | https://api.cristin.no/v2/units/194.31.15.15 |

  Scenario Outline: The user downloads a single subsubsubunit in Norwegian Bokmål
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>?language=nb
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that has a single subsubsubunit
    And they see the subsubsubunit is a single object contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Bokmål

    Examples:
      | Unit IRI                                     |
      | https://api.cristin.no/v2/units/194.31.15.15 |

  Scenario Outline: The user downloads a single subsubsubunit in Norwegian Nynorsk
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=nn
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that has a single subsubsubunit
    And they see the subsubsubunit is a single object contains the <Unit IRI>
    And they see that the texts in the response body are mostly in Norwegian Nynorsk
    Examples:
      | Unit IRI                                     |
      | https://api.cristin.no/v2/units/194.31.15.15 |

  Scenario Outline: The user downloads a single subsubsubunit in English
    Given that the user has a valid <Unit IRI>
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>&language=en
    Then they receive a response with status code 200
    And they see that the response Content-type is "application/json"
    And they see the body of the response contains a JSON object that is a nested representation of the institution
    And they see the object is a top-level institution object that has a single subunit
    And they see the subunit is a single object that has a single subunit
    And they see the subsubunit is a single object that has a single subsubsubunit
    And they see the subsubsubunit is a single object contains the <Unit IRI>
    And they see that the texts in the response body are mostly in English

    Examples:
      | Unit IRI                                     |
      | https://api.cristin.no/v2/units/194.31.15.15 |

  Scenario Outline: The user requests a subunit that does not exist
    Given that the user has an invalid <Unit IRI>
    When they set the request Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>
    Then they receive an response with status code 404
    And they see that the response Content-type is "application/problem+json"
    And they see the response body contains a problem.json object
    And they see the response body has a field "title" with the value "Not found"
    And they see the response body has a field "status" with the value "404"
    Examples:
      | Unit IRI                    |
      | https://example.org/1.1.1.1 |

  Scenario Outline: The third party service is unavailable
    Given that the third party service is unavailable
    When they set the Accept header to "application/json"
    And they request /institution?iri=<Unit IRI>
    Then they receive a response with status code 502
    And they see that the response Content-type is "application/problem+json"
    And they see the response body contains a problem.json object
    And they see the response body has a field "title" with the value "Bad gateway"
    And they see the response body has a field "status" with the value "502"
    And they see the response body has a field "detail" with the value "The institution service is unavailable"

    Examples:
      | Unit IRI                                     |
      | https://api.cristin.no/v2/units/194.31.15.15 |
