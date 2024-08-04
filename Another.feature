Feature: Another feature file

  @addPlace @regression
  Scenario Outline: Verify adding a new place <name>
    Given User builds an add place payload with "<name>", "<language>", "<address>"
    When User "post" http request to "addPlaceAPI"
    Then User gets a http success code as 200
    And User gets a "status" in response body as "OK"
    And User gets a "scope" in response body as "APP"
    Then User verifies "<name>" using "getPlaceAPI"

    Examples: 
      | name | language | address         |
      | ABC  | Germany  | Some Address    |
      | DEF  | India    | Another Address |

  @deletePlace @regression
  Scenario: Verify delete place functionality
    Given User builds delete payload
    When User "post" http request to "deletePlaceAPI"
    Then User gets a http success code as 200
    And User gets a "status" in response body as "OK"
    
  @location
  Scenario: Verify graphQL location details
  	Given User builds location payload
  	Then User posts the location request
  	
  @createLocation
  Scenario: Verify graphQL create location
  	Given User builds create location payload
  	Then User posts the create location request

  
  #"{\n  location(locationId: 11420) {\n    id\n  }\n}\n"