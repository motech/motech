==================
Coding Conventions
==================

.. contents:: Table of Contents
   :depth: 4

############
Introduction
############

Goals
#####

The primary goal of the MOTECH coding guidelines is to minimize the cost of maintaining and innovating on code through
its lifetime.

Additional goals are:

- Increased developer efficiency when working with code written by others.
- Promoting engineering excellence.
- Compliance with legal and company policies.
- Consistency of the codebase.

There are many guidelines in this document. The decision to introduce a guideline as well as choices between
alternative approaches was informed by the following assumptions:

- The benefit of each guideline must significantly surpass the cost of enforcing it.
- During its lifetime, each unit of code is read many more times than it is written or modified.
  Therefore, the guidelines are optimized for code readability. Cost of writing or modifying code is a secondary
  consideration.

Many of the following guidelines are adapted from `Sun's Java Coding Conventions document (PDF)
<http://www.oracle.com/technetwork/java/codeconventions-150003.pdf>`_.


Guideline Presentation
######################

The guidelines are organized as simple recommendations using **Do**, **Consider**, **Avoid**, and **Do not**.
Each guideline describes either a good or bad practice and all have a consistent presentation.
Good practices have a check (|v|) in front of them, and bad practices have an 'x' (|x|) in front of them.
The wording of each guideline also indicates how strong the recommendation is.

A **Do** guideline is one that should be always followed.

On the other hand, **Consider** guidelines should be generally followed, but if you fully understand the
reasoning behind a guideline and have a good reason to not follow it anyway, it is ok to break the rule.

Similarly, **Do not** guidelines indicate something you should never do.

Less strong, **Avoid** guidelines, indicate that something is generally not a good idea, but there are known
cases where breaking the rule makes sense.

Some more complex guidelines are followed with additional background information, illustrative code samples,
and rationale.

#################
Coding Guidelines
#################

Naming
######

Class and Interface Names
-------------------------
|v| **Do** use PascalCasing (the first letter of each internal word is capitalized) for interface,
and class names. Class names should be nouns. Keep your class names simple and descriptive. Use whole words — avoid
acronyms and abbreviations. If the abbreviation is much more widely used than the long form, such as URL or HTML,
capitalize only the first letter of the acronym.

.. code-block:: java

    class Raster;
    class SurveyResults;
    class HtmlView;

Service Interfaces & Implementations
------------------------------------
|v| **Do** append Impl to the implementations class names and place the implementations in a /impl/ subdirectory. So for example
the Foo service interface should have the following structure:

 /
     Foo.java
     /impl/
         FooImpl.java

Method Names
------------
|v| **Do** use camelCasing for method names (the first letter is lowercase, with the first letter of each additional word
capitalized). Methods should be verbs, for example:

.. code-block:: java

    run();
    runFast();
    getBackground();


Variables
---------
All instance, class, and class variables are in camelCase. Additional words start with capital letters.
Variable names should be short yet meaningful. The choice of a variable name should be mnemonic — that is,
designed to indicate to the casual observer the intent of its use. One-character variable names should be avoided
with the possible exception of temporary "throwaway" variables, e.g. for loops. Even in these cases, more readable
names can be provided (e.g. "surveyIndex" instead of "i").

|x| **Do not** use a prefix for member fields or methods (for example do not start your names with: underscore, m, s, etc.)

|v| **Do** use camelCasing for member variables

|v| **Do** use camelCasing for parameters

|v| **Do** use camelCasing for local variables

|x| **Do not** prefix enums or classes with any letter

Correct:

.. code-block:: java

    public class Button

Incorrect:

.. code-block:: java

    public class CButton

|x| **Do not** make local declarations that hide declarations at higher levels. For example, do not declare a previously
occurring variable name in an inner block:

.. code-block:: java

    int count;
    ...
    func() {
        if (condition) {
            int count; // DON'T DO THIS!
            ...
        }
        ...
    }

|x| **Do not** declare more than one variable per line, even if the language supports it.

Correct:

.. code-block:: java

    int startIndex;
    int endIndex;

Incorrect:

.. code-block:: java

    int startIndex, endIndex;

|x| **Do not** assign a value to more than one variable per statement, even if the language supports it.

Correct:

.. code-block:: java

    int surveyCount = 10;
    int farmerCount = 10;

Incorrect:

.. code-block:: java

    int surveyCount = farmerCount = 10;

Constants
---------
|v| **Do** name constants with all uppercase words separated by underscores.

.. code-block:: java

    int MIN_WIDTH = 4;
    int MAX_WIDTH = 999;


Enum Values
-----------
|v| **Do** name enum values the same way as constants - all uppercase, with words separated by underscores.

.. code-block:: java

    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY
    }

File Names
----------
|x| **Do not** have more than one public type in a source file. Each Java source file contains a single public class
or interface.

|v| **Do** name the source file with the name of the type it contains. For example, MotechScheduler class
should be in the MotechScheduler.java file.

|v| **Do** use the same casing when mapping the type name to file name.

File Content
------------
|v| **Do** put package and import statements (in that order) directly following the copyright banner, and prior to the
class definition:

.. code-block:: java

    import java.applet.Applet;
    import java.util.List;
    import java.util.Map;

|v| **Do** group class members into the following sections in the specified order:

1. Static fields
2. Instance fields
3. Constructors
4. Methods
5. Inner classes

|v| **Do** order fields by public, then protected, then private.

|v| **Do** group methods by related functionality.

|v| **Consider** organizing overloads from the simplest to the most complex number of parameters (which often
corresponds to complexity of the body).

|x| **Do not** declare imports not used within the file.

Element ID
----------
To allow for easier integation testing of MOTECH-UI, we are using a naming convention for the element IDs of navigational and action based items in the UI. The element IDs should follow the format below, unless there is a clear reason the format shouldn't be followed.

|v| **Do** use lowercase names only.

|v| **Do** replace spaces with dashes.

|v| **Do** keep names as short as possible, while using full english words.

|v| **Do** use following pattern:

{module}.{location}.{entity-id}.{error}.{action}

1. Module
Module refers to the OSGI bundle or AngularJS module name that the element belongs to. 

|x| **Do not** start name with "motech".
If the module name starts with "motech" that should be omitted.

Correct:

.. code-block:: html
	<element id="dashboard.sidebar.settings"/>

Incorrect:

.. code-block:: html
	<element id="motech-dashboard.sidebar.settings"/>

2. Location
Location refers to where in the interface the html element is located. This location is relative to the module, not with absolute relation to the entire interface.
For tasks breadcrumb would be:

.. code-block:: html
	<element id="tasks.breadcrumb.tasks"/>

3. Entity Id (optional)
The entity id refers to a repeating id which is used in lists of links. 

|v| **Consider** using appropriate non-numeric id, in preference to a numeric id.

4. Error (optional)
If element is used for error messages use ".errors." before action.

.. code-block:: html
	<ul id="email.send.subject-errors">
		<li id="email.send.subject.errors.length">message</li>
		<li id="email.send.subject.errors.required">message</li>
	</ul>
	
5. Action
Action should describe what the button, input, or form does. 
If this is a link it should refer to where the link goes.

.. code-block:: html
	<element id="email.compose.send"/>
	<element id="mds.nav.browser"/>
	<element id="mds.nav.schema-editor"/>
	<element id="task.sidebar.tasks-toggle-active"/>


Common Conventions for API creation
###################################

HTTP methods and endpoints
--------------------------

|v| **Do** use the following, general pattern for endpoints:

``/{resource}``

``/{resource}/{path-variable}``

Path can take params.

|v| **Do** use HTTP methods verbs:

- GET for read resource by id or collection.
- PUT to update resource by id.
- DELETE to delete resource by id.
- POST for create new resources and other operations.

|v| **Do** use plurals in resource names.

Correct:

``GET /shoes/<id>``

Incorrect:

``GET /shoe/<id>``

|x| **Avoid** use verbs in URL if there is equivalent in HTTP methods.

Correct:

``DELETE /shoes/<id>``

``PUT /shoes/<id>``

``POST /shoes/<id>/sell``

Incorrect:

``POST /shoes/<id>/delete``

``PUT /shoes/<id>/update``


|x| **Avoid** use collection words in URL.

Correct:

``GET /shoes``

Incorrect:

``GET /shoe/list``

Response codes
--------------

|v| **Do** use response codes:

- **200** *OK* - For successful operation.
- **400** *Bad Request* - The body or parameters provided in the request are invalid.
- **401** *Unauthorized* - The caller is not authorized and thus not permitted to execute the operation.
- **403** *Forbidden* - The user does not have necessary rights to execute the operation.
- **404** *Not Found* - Either the given entity or the requested object does not exist.

Code Comments
#############

|v| **Do** use code comments to document code whose operation is not self-evident to the professional
developer (e.g. code reviewer). For example, consider commenting:

- Pre-conditions not evident in code, e.g. thread-safety assumptions
- Complex algorithms
- Complex flow of control, e.g. chained asynchronous calls
- Dependencies on global state
- Security considerations
- Return values, e.g. returning either an object or null
- DateTime parameters, are we expecting UTC or local date/times, or is the timezone encapsulated in the DateTime object?

|x| **Avoid** using comments that repeat self-commenting information found in many code structures. For example,
do not add vacuous comments such as "Constructors", "Properties", "Using Statements". Avoid commenting:

- Type declarations (e.g. method signatures)
- Assertions
- Method overloads
- Well-understood patterns (e.g. enumerators)

|v| **Do** use Javadoc comments before your public field and method definitions.

.. code-block:: java

    /**
     * Short one line description.
     *
     * Longer description. If there were any, it would be
     * here.
     *
     * @param  variable Description text text text.
     * @return Description text text text.
     */

|v| **Do** use // commenting style for both single and multi-line prose comments. For example:

.. code-block:: java

    // This method assumes synchronization is done by the caller
    Byte[] ReadData(Stream stream)

or

.. code-block:: java

    // This AsyncResult implementation allows chaining of two
    // asynchronous operations. It executes the second operation only
    // after the first operation completes.

|x| **Avoid** leaving unused code in a file, for example by commenting it out. There are occasions when leaving unused
code in a file is useful (for example implementing a single feature over multiple checkins), but this should be rare
and short in duration.

|x| **Avoid** using #if/#endif commenting style for purposes other than excluding code from the compilation process:

.. code-block:: c#

    Console.WriteLine(“Hello”);
    #if false
        Console.WriteLine(“Press  to continue...”);
        Console.Readline();
    #endif
        Console.WriteLine(“Finished”);

Syntax
######

Braces
------
|v| **Do** use braces with if, else, while, do, and dowhile statements.

|x| **Do not** omit braces, even if the language allows it.

Braces should not be considered optional. Even for single statement blocks, you should use braces. This increases code
readability and maintainability.

.. code-block:: java

    for (int i = 0; i < 100; i++) {
        doSomething(i);
    }

The only exception to the rule is braces in case statements. These braces can be omitted as the case and break
statements indicate the beginning and the end of the block.

.. code-block:: java

    case 0:
        doSomething();
        break;

|v| **Do** place opening braces on the same line as their associated statement, with a space before the opening brace.

|v| **Do** place closing braces in their own line.

|v| **Do** align the closing brace with its corresponding opening statement.

.. code-block:: java

    if (someExpression) {
        doSomething();
    }

Indents and Tabs
----------------
|v| **Do** use 4 consecutive space characters for indents.

|x| **Do not** use the tab character for indents.

|v| **Do** indent contents of code blocks.

.. code-block:: java

    if (someExpression) {
        doSomething();
    }

|v| **Do** indent case blocks even if not using braces.

.. code-block:: java

    switch (someExpression) {
        case 0:
            doSomething();
            break;
    }

Spacing
-------
|v| **Do** use a single space after a comma between function arguments.

Correct:

.. code-block:: java

    read(myChar, 0, 1);

Incorrect:

.. code-block:: java

    read(myChar,0,1);

|x| **Do not** use a space after the parenthesis and function arguments

Correct:

.. code-block:: java

    createFoo(myChar, 0, 1)

Incorrect:

.. code-block:: java

    createFoo( myChar, 0, 1 )

|x| **Do not** use spaces between a function name and parenthesis.

Correct:

.. code-block:: java

    createFoo()

Incorrect:

.. code-block:: java

    createFoo ()

|x| **Do not** use spaces inside brackets.

Correct:

.. code-block:: java

    x = dataArray[TDG:index];

Incorrect:

.. code-block:: java

    x = dataArray[TDG: index ];

|v| **Do** use a single space before flow control statements

Correct:

.. code-block:: java

    while (x == y)

Incorrect:

.. code-block:: java

    while(x==y)

|v| **Do** use a single space before and after comparison operators

Correct:

.. code-block:: java

    if (x == y)

Incorrect:

.. code-block:: java

    if (x==y)

|v| **Do** use a single space before and after arithmetic operators

Correct:

.. code-block:: java

    x = x + y;

Incorrect:

.. code-block:: java

    x = x+y;

|v| **Do** use a single space before and after assignment operations

Correct:

.. code-block:: java

    x = y;

Incorrect:

.. code-block:: java

    x=y;

|v| **Do** use a space or newline before and after the conditional operator

Correct:

.. code-block:: java

    x = ((p > q) ? y : z);

Incorrect:

.. code-block:: java

    x = (p > q)?y:z;

|v| **Do** use parenthesis around the conditional operator

Correct:

.. code-block:: java

    x = (foo ? y : z);

Incorrect:

.. code-block:: java

    x = foo ? y : z;

|v| **Do** use a single space for class derivation

Correct:

.. code-block:: java

    class Button extends Control

|v| **Do** use a single space for variable declarations.

|x| **Do not** use multiple spaces to try and align variable names separately from their types.

Correct:

.. code-block:: java

    int groupSize = 10;

|v| **Do** use a single blank line in between method definitions.

.. |v| image:: img/checkmark.png
.. |x| image:: img/x.png


Page Width
##########

|v| **Do** try to limit the width of your code to 120 characters.

|v| **Do** Use common sense. If changing an existing file with obvious 80 column formatting keep it that way. If a
particular line will be much more readable but break the width rule, use common sense.


Changing Existing Code
######################

|v| **Do** comply with the 'when in Rome, do as the Romans do' principle. When working on an existing file, please limit
your changes to the issue you're working on so as to not overwhelm the person reviewing your code with unnecessary
changes.

|v| **Do** feel responsible to fix a really messy file. Making overall changes to a file to make it look good, outside
the needs of your actual change, is an acceptable exception to the preceding rule when dealing with a real mess.
