# Introduction

-----------------------
Here are the conventions that will be accepted by the dev team and that should be updated when necessary. 
The Style Guide is split in 2 parts: JavaScript and Java. 

JavaScript part is initiated from [Airbnb Style Guide](https://github.com/airbnb/javascript).

# Table of Contents
   1. [References](#references)
   1. [Objects](#objects)
   1. [Arrays](#arrays)
   1. [Destructuring](#destructuring)
   1. [Strings](#strings)
   1. [Functions](#functions)
   1. [Arrow Functions](#arrow-functions)
   1. [Modules](#modules)
   1. [Variables](#variables)
   1. [Hoisting](#hoisting)
   1. [Comparison Operators & Equality](#comparison-operators--equality)
   1. [Whitespace](#whitespace)
   1. [Commas](#commas)
   1. [Semicolons](#semicolons)
   1. [Type Casting & Coercion](#type-casting--coercion)
   1. [Events](#events)
# JavaScript style
<a name="references"></a>
## References

Use `const` for all of your references; avoid using `var`
> Why? This ensures that you can’t reassign your references, which can lead to bugs and difficult to comprehend code.

If you must reassign references, use `let` instead of `var`
> Why? let is block-scoped rather than function-scoped like var.

<a name="objects"></a>
## Objects
Use the literal syntax for object creation. eslint: `no-new-object` <br>
bad: `const item = new Object()`
good: `const item = {}`

Use property value and object method shorthands. Put the shorthands at the top of the object declaration. eslint: `object-shorthand` <br>
bad:<br>
```
const lukeSkywalker = 'Luke Skywalker'
const atom = {
   value: 1,
   lukeSkywalker: lukeSkywalker,
   addValue: function (value) {
     return atom.value + value
   }
 }
 ```
 good: <br>
 ```
 const atom = {
   lukeSkywalker,
   addValue(value) {
     return atom.value + value
   }
   value: 1,
 }
 ```
 > Why? It is shorter and descriptive.
 
 <a name="arrays"></a>
 ## Arrays
 Use the literal syntax for array creation. eslint: `no-array-constructor` <br>
 bad: `const items = new Array()` <br>
 good: `const items = []` <br>
 
 Use array spreads `...` to copy arrays <br>
 good: `const itemsCopy = [...items]`
 
 <a name="destructuring"></a>
 ## Destructuring
 Use object destructuring when accessing and using multiple properties of an object. eslint: `prefer-destructuring`
 
 > Why? Destructuring saves you from creating temporary references for those properties.
 ```
// bad
function getFullName(user) {
  const firstName = user.firstName
  const lastName = user.lastName

  return `${firstName} ${lastName}`
}

// good
function getFullName(user) {
  const { firstName, lastName } = user
  return `${firstName} ${lastName}`
}

// best
function getFullName({ firstName, lastName }) {
  return `${firstName} ${lastName}`
}
```

Use array destructuring. eslint: `prefer-destructuring`
```
const arr = [1, 2, 3, 4]

// bad
const first = arr[0]
const second = arr[1]

// good
const [first, second] = arr
```

<a name="strings"></a>
## Strings
Use single quotes ' ' for strings. eslint: `quotes`

<a name="functions"></a>
## Functions
Use default parameter syntax rather than mutating function arguments.
```
// really bad
function handleThings(opts) {
  // Double bad: if opts is falsy it'll be set to an object which may
  // be what you want but it can introduce subtle bugs.
  opts = opts || {}
  // ...
}

// still bad
function handleThings(opts) {
  if (opts === void 0) {
    opts = {}
  }
  // ...
}

// good
function handleThings(opts = {}) {
  // ...
}
```

Always put default parameters last.
```
// bad
function handleThings(opts = {}, name) {
  // ...
}

// good
function handleThings(name, opts = {}) {
  // ...
}
```
<a name="arrow-functions"></a>
## Arrow Functions
When you must use an anonymous function (as when passing an inline callback), use arrow function notation. eslint: `prefer-arrow-callback`, `arrow-spacing`
> Why? It creates a version of the function that executes in the context of this, which is usually what you want, and is a more concise syntax.

```
// bad
[1, 2, 3].map(function (x) {
  const y = x + 1
  return x * y
})

// good
[1, 2, 3].map((x) => {
  const y = x + 1
  return x * y
})
```

<a name="modules"></a>
## Modules
Always use modules (import/export) over a non-standard module system. You can always transpile to your preferred module system.

```
// bad
const AirbnbStyleGuide = require('./AirbnbStyleGuide')
module.exports = AirbnbStyleGuide.es6

// ok
import AirbnbStyleGuide from './AirbnbStyleGuide'
export default AirbnbStyleGuide.es6

// best
import { es6 } from './AirbnbStyleGuide'
export default es6
```

Do not use wildcard imports.

> Why? This makes sure you have a single default export.
```
// bad
import * as AirbnbStyleGuide from './AirbnbStyleGuide'

// good
import AirbnbStyleGuide from './AirbnbStyleGuide'
```

And do not export directly from an import.

> Why? Although the one-liner is concise, having one clear way to import and one clear way to export makes things consistent.
```
// bad
// filename es6.js
export { es6 as default } from './AirbnbStyleGuide'

// good
// filename es6.js
import { es6 } from './AirbnbStyleGuide'
export default es6
```

<a name="variables"></a>
## Variables 
Use one const or let declaration per variable or assignment. eslint: `one-var` and `one-var-declaration-per-line`

> Why? It’s easier to add new variable declarations this way, and you never have to worry about swapping out a ; for a , or introducing punctuation-only diffs. You can also step through each declaration with the debugger, instead of jumping through all of them at once.
```
// bad
const items = getItems(),
    goSportsTeam = true,
    dragonball = 'z';

// bad
// (compare to above, and try to spot the mistake)
const items = getItems(),
    goSportsTeam = true;
    dragonball = 'z';

// good
const items = getItems();
const goSportsTeam = true;
const dragonball = 'z';
```

Disallow unused variables. eslint: `no-unused-vars`

> Why? Variables that are declared and not used anywhere in the code are most likely an error due to incomplete refactoring. Such variables take up space in the code and can lead to confusion by readers.

<a name="comparison-operators--equality"></a>
## Comparison Operators & Equality
Use `===` and `!==` over `==` and `!=`. eslint: `eqeqeq`

Ternaries should not be nested and generally be single line expressions. eslint: `no-nested-ternary`
```
// bad
const foo = maybe1 > maybe2
  ? "bar"
  : value1 > value2 ? "baz" : null

// good
const foo = maybe1 > maybe2 ? 'bar' : maybeNull
```

<a name="whitespace"></a>
## Whitespace
Use indentation when making long method chains (more than 2 method chains). Use a leading dot, which emphasizes that the line is a method call, not a new statement. eslint: `newline-per-chained-call` `no-whitespace-before-property`
```
// bad
$('#items').find('.selected').highlight().end().find('.open').updateCount()

// good
$('#items')
  .find('.selected')
    .highlight()
    .end()
  .find('.open')
    .updateCount()
```

Tab and indentation length should be 2. eslint: `max-len` `indent`
Avoid having lines of code that are longer than 100 characters (including whitespace). eslint: `max-len`

> Why? This ensures readability and maintainability.

<a name="commas"></a>
## Commas
Additional trailing comma: Nop. eslint: `comma-dangle`

<a name="semicolons"></a>
## Semicolons
Use of semicolons: NO ( different from airbnb style guide after long debate ). eslint: `semi`
Multiple examples to illustrate [why](https://github.com/airbnb/javascript#semicolons)

<a name="type-casting--coercion"></a>
## Type Casting & Coercion
Booleans: eslint: `no-new-wrappers`
```
const age = 0

// bad
const hasAge = new Boolean(age)

// best
const hasAge = !!age
```
<a name="events"></a>
## Events

When attaching data payloads to events (whether DOM events or something more proprietary like Backbone events), pass an object literal (also known as a "hash") instead of a raw value. This allows a subsequent contributor to add more data to the event payload without finding and updating every handler for the event. For example, instead of:
```
// bad
$(this).trigger('listingUpdated', listing.id);

// ...

$(this).on('listingUpdated', (e, listingID) => {
  // do something with listingID
});
```
prefer:
```
// good
$(this).trigger('listingUpdated', { listingID: listing.id });

// ...

$(this).on('listingUpdated', (e, data) => {
  // do something with data.listingID
});
```
# Java style
    Loading...