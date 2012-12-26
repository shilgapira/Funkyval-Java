# Funkyval

Quick and dirty expression evaluator I hacked together. It's pretty handy for application state logic that's determined in runtime, most likely as input from a web service.

Funkyval evaluates arithmetic, boolean and string expressions with support for getting and setting values from variables.

Should work on any Android version as it's just plain Java.


## Usage

``` java
// Container for variables and their values
Map<String, String> variables = ...;

...

// Simple evaluation of a math expression
Funkyval mult = Funkyval.fromExpression("2 * 4");
int result = mult.evaluateInteger(variables); // 8

...

// Using variables for state logic
variables.put("door", "open");

Funkyval foo = Funkyval.fromExpression("door == open");
if (foo.evaluateBoolean(variables)) {
	// close the door
	Funkyval.fromExpression("door = closed").perform();
}

...

// Get expression from web service
String validatorExpression = json.get("validator");

Funkyval validator = Funkyval.fromExpression(validatorExpression);

Map<String, String> userData = ...;
if (validator.evaluateBoolean(userData)) {
	// success
} else {
	// error
}

```


## Expressions

- ```open == true```
- ```open = false```
- ```!open```
- ```state = (8 * 4)```
- ```state++```
- ```state >= 1```
- ```(state >= 1) && (open == true)```
- ```state = 0, open = false```
- ```(number % 2) == 1```
- ```(2 + 2) == 4```
- ```...```

Check the unit test out for more examples.


## Why?

Nothing groundbreaking here as there are many libraries that do all of this. However, as is often the case they do much more than what I needed and we prioritize small binary size. The Funkyval .class files take about 12KB of space after Proguarding, so there's that.


## Limitations

- Doesn't support operator precedence so use parentheses liberally.
- Values are treated as numbers, booleans or strings depending on context and what makes more sense (subjectively).

Then again if these actually turn out to be issues for your use case then you'll probably be better served by a more complete solution.
