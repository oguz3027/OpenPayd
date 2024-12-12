Feature: Acceptance criteria implementation

  @SearchProduct
  Scenario: Check non-discounted products on the cart
    Given Navigate to amazon web site
    Then Check the homepage
    When Search by word "laptop"
    And Add the non-discounted products in the stock on the first page of the search results to the cart
    And Go to cart
    Then Verify products added correctly