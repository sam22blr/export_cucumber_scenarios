Feature: Another feature two file

  @addPlace @regression
  Scenario Outline: Verify adding a new place <name>
    Given User builds an add place payload with "<name>", "<language>", "<address>"
 

    Examples: 
      | name | language | address         |
      | ABC  | Germany  | Some Address    |
      | DEF  | India    | Another Address |

