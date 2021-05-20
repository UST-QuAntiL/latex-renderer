# LaTex-Renderer
Service for rendering latex content such as algorithms, diagrams, pictures etc. Latex-Render accepts the document body, the packages that are required to render the document and offers pdf, svg and png as output formats.

### Version:
The latest version is: **1.1**


### Installation:
* As a **Docker Container** (highly recommended)
	1. Pull and Run image via: *docker run --rm -p 8082:8082 planqk/latex-renderer:latest

* As a **Spring-Boot** project:

  Requirements:
	* Full latex distribution (for example miktex or texlive)

  Install:
	1. Download Latex-Renderer
	3. 1st time Setup: *mvn package -DskipTests*
	4. To start Latex-Renderer run: *mvn spring-boot:run*

### API:
Runs on Port 8082.

It is possible to Request the Data with either a POST- or a GET-Request
* POST: http://localhost:8082/renderLatex  expects (String content, List<String> latexPackages, String output) as JSON
* GET: http://localhost:8082/renderLatex expects (String content, List<String> latexPackages, String output) must be [URLEncoded](http://www.eso.org/~ndelmott/url_encode.html)

Output formats are:
* pdf: returns a PDF document that contains a cutout of the rendered content.
* png: returns a PNG that contains the cutout of the rendered content.
* svg: returns a SVG that contains the cutout of the rendered content.
* fullPdf: returns a PDF document that contains the rendered content on ISO A4 pages.


POST-Request example:
``{"content":"\\begin{quantikz} \\lstick{\\ket{0}} & \\phase{\\alpha} & \\gate{H} & \\phase{\\beta} & \\gate{H} & \\phase{\\gamma} & \\rstick{Arbitrary\\\\pure state}\\qw \\end{quantikz}","latexPackages":["\\usepackage{tikz}","\\usetikzlibrary{quantikz}"],"output":"pdf"}``

GET-Request example: ``http://localhost:8082/renderLatex?packages=%5Cusepackage%7Btikz%7D&packages=%5Cusetikzlibrary%7Bquantikz%7D&output=svg&content=%5Cbegin%7Bquantikz%7D+%5Clstick%7B%5Cket%7B0%7D%7D+%26+%5Cphase%7B%5Calpha%7D+%26+%5Cgate%7BH%7D+%26+%5Cphase%7B%5Cbeta%7D+%26+%5Cgate%7BH%7D+%26+%5Cphase%7B%5Cgamma%7D+%26+%5Crstick%7BArbitrary%5C%5Cpure+state%7D%5Cqw+%5Cend%7Bquantikz%7D
``

### Userguide:
* This service is intended for automated latex rendering.
* The Latex-Renderer can render maths, but it is HIGHLY recommended to use Mathjax or Katex for maths in web applications.
* When sending content, always make sure that all required packages are included in the *latexPackages* field.
  *latexPackages* expects an array of declarations. Therefore, list your packages and other declarations in, for example an ArrayList.
* The *content* string should be prepared properly before sending, so the latex compiler won't have any issues because of missing spaces or linebreaks. Follow the instructions in the *Notes* section to make sure your latex string works.



### Notes:
* LatexRenderer creates a new latex document with the following structure:
  \documentclass{standalone/article}  
  INSERT SENT LATEXPACKAGES  
  \begin{document}  
  INSERT SENT CONTENT  
  \end{document}  
  This means that all declarations etc. that need to be inserted before `\begin document` must be included in latexPackages.
* The content and latexPackages must strictly follow the JSON guidelines. This means that all Backslashes, Doublequotes, Newlines etc. must be escaped. Furthermore, is it required that the content of one field is within one line.
* Necessary Newlines must explicitly be defined with \n. For example in a listing or after a comment: *$a^n$ %this is important **\n***.
* All cutout formats (pdf, png, svg) use `standalone` to crop the content. Standalone uses horizontal mode, which doesn't allow for lists or paragraphs. If you want to render lists, tables etc. use the fullPdf export option.
* Equations can be rendered as `standalone` when using $-Signs to indicate the math environment.
