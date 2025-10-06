# Concordion Usage Advice #

There was an issue related to the execute-assign statement in html files.

To produce this issue, you can add the following code in a html file:

```html
<p c:set="#myValue" c:execute="myMethodThatReturnsAvalue()" ></p>
<p c:echo="#myValue"></p>
```

The expected behavior is that the value returned by the method `myMethodThatReturnsAvalue()` 
is assigned to the variable `myValue` and then echoed in the second paragraph. However the right
syntax to use is:

```html
<p c:execute="#myValue = myMethodThatReturnsAvalue()" ></p>
<p c:echo="#myValue"></p>
```

Also, if the method returns a value you can use directly:

```html
<p c:echo="myMethodThatReturnsAvalue()" ></p>
```