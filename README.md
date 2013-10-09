libGDX Path Editor
=======================================

libGDX Path Editor lets you easily create complex pathes your game characters can move along.
It uses a Catmull-Rom spline to construct a path curve between all vertices that you define via WYSIWYG editor.
The result path is a list of all spline vertices (control and intermediate) saved in XML/JSON file, for each vertex its position and rotation angle are stored.

How to use
----------

1. Create a new project, specify destination folder and project name.

2. Now add a first screen, specify screen name and size.

3. Add a background image to your screen, specify its name, texture and texture scale coefficient.
   Background image should contain a visual path which needs to be represented as a set of vertices.

4. Now you can create a path as a spline by defining its control vertices.
   Turn on "Add vertex" mode, specify path name, intermediate vertices number (between each pair of control vertices), select color of control, intermediate and selected vertices.
   Add first 4 vertices (the minimum number required to create a spline) by clicking on a visual path on a background image - and the first spline's segment would appear.
   By adding more and more control vertices you create a whole path. When path is ready, turn off "Add vertex" mode.

5. You can change the control vertex position by turning on "Move vertex" mode, click on a vertex (it becomes highlighted) and drag it to a new location. When done, turn off "Move vertex" mode.

6. You can insert a new control vertex between a two neighboor vertices by turning on "Insert vertex" mode.
   Select the first and the second vertices and click somewhere outside the path - a new control vertex will be added between them. When done, turn off "Insert vertex" mode.

7. You can remove a selected control vertex by turning on "Remove vertex" mode and clicking on a desired vertex. When done, turn off "Remove vertex" mode.

8. You can remove a whole path by clicking on "Clear Path" button.

Right now you can add only one path per screen, but you can have many screens in one project.

There is a sample project in the /demo folder.
