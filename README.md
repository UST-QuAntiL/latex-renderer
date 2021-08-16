# LaTex-Renderer
Service for rendering latex content such as algorithms, diagrams, pictures etc. Latex-Render accepts the document body, the packages that are required to render the document and offers svg, pdf, jpg and png as output formats.

### Version:
The latest version is: **1.1.0**


### Installation:
* As a **Docker Container** (highly recommended)
	1. Pull and Run image via: ``docker run --rm -p 5030:5030 planqk/latex-renderer:latest``

* As a **Spring-Boot** project:

  Requirements:
	* Full latex distribution (for example miktex or texlive)

  Install:
	1. Download Latex-Renderer
	3. 1st time setup: ``mvn package -DskipTests``
	4. To start Latex-Renderer run: ``mvn spring-boot:run``

### API:
Runs on Port 5030.

It is possible to request the data with either a POST- or a GET-Request
* POST: http://localhost:5030/renderLatex  expects (String content, List<String> latexPackages, String output) as [JSON](https://www.json.org/json-en.html)
* GET: http://localhost:5030/renderLatex expects (String content, List<String> latexPackages, String output) must be [URLEncoded](http://www.eso.org/~ndelmott/url_encode.html)

**Output formats** are:
* svg (default): returns a SVG that contains the cutout of the rendered content.
* pdf: returns a PDF document that contains a cutout of the rendered content.
* png: returns a PNG that contains the cutout of the rendered content.
* jpg: returns a JPEG that contains the cutout of the rendered content.
* fullPdf: returns a PDF document that contains the rendered content on ISO A4 pages.

**Required** attributes: 
* *content* (default: None, type: String): The LaTeX documentcontent that shall be rendered.

**Optional** attributes: 
* *packages* (default: None, type: List<String>): List of packages that is required for rendering the content 
* *output* (default: "svg", type: String): Rendering output format, according to description above
* *varwidth* (default: None, type: Number/Float): When rendering text it is recommended to set varwidth. Varwidth manages the linebreaks for the standalone package which is used for all output types but "fullpdf". 
Further varwidth guarantees a maximum document width of varwidth\textwidth, meaning a value of 1 will render the documentcontent on a DINA4 page.

### Userguide:
* This service is intended for automated latex rendering.
* The Latex-Renderer can render maths, but it is HIGHLY recommended to use Mathjax or Katex for maths in web applications.
* When sending content, always make sure that all required packages are included in the *latexPackages* field.
  *latexPackages* expects an array of declarations. Therefore, list your packages and other declarations in, for example an ArrayList.
* The *content* string should be prepared properly before sending, so the latex compiler won't have any issues because of missing spaces or linebreaks. Follow the instructions in the *Notes* section to make sure your latex string works.


#### Examples:
A minimal GET-Request example to render *pi* without defining any packages or an ouput format: ``http://localhost:5030/renderLatex?content=$%5Cpi$``

A POST-Request example that sets the output format to *pdf*, includes the two necessary packages *\usepackage{tikz} and \usetikzlibrary{quantikz}* and sets the content to render a small Quantum Circuit example:
```json
{"content":"\\begin{quantikz} \\lstick{\\ket{0}} & \\phase{\\alpha} & \\gate{H} & \\phase{\\beta} & \\gate{H} & \\phase{\\gamma} & \\rstick{Arbitrary\\\\pure state}\\qw \\end{quantikz}","latexPackages":["\\usepackage{tikz}","\\usetikzlibrary{quantikz}"],"output":"pdf"}
```
The same example as before just sent via a GET-Request:
``http://localhost:5030/renderLatex?packages=%5Cusepackage%7Btikz%7D&packages=%5Cusetikzlibrary%7Bquantikz%7D&output=pdf&content=%5Cbegin%7Bquantikz%7D+%5Clstick%7B%5Cket%7B0%7D%7D+%26+%5Cphase%7B%5Calpha%7D+%26+%5Cgate%7BH%7D+%26+%5Cphase%7B%5Cbeta%7D+%26+%5Cgate%7BH%7D+%26+%5Cphase%7B%5Cgamma%7D+%26+%5Crstick%7BArbitrary%5C%5Cpure+state%7D%5Cqw+%5Cend%7Bquantikz%7D
``

When rendering pictures and text in one request it is recommended to set varwidth and additionally add ``\usepackage{adjustbox}`` to the package list. 
Make sure to surround each graphic with a ``begin`` and ``end`` block of the adjustbox environment as follows:
  ```latex
  Text before the graphic
  \begin{adjustbox}{max width=\textwidth}
    GRAPHIC HERE
  \end{adjustbox}
  Text following the graphic
  ```
A JSON example would look as follows:
```json
{"content":"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, \n\n\n\\begin{adjustbox}{max width=\textwidth}\n\\begin{quantikz}[row sep=0.1cm]& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\&  \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\gate{X} & \\ctrl{1} & \\gate{X} & \\qw \\\\& \\qw & \\control{} & \\qw & \\qw \\\\& \\gate{X} & \\qw & \\qw & \\qw \\\\& \\gate{H} & \\qw & \\qw & \\qw\\end{quantikz}\n\\end{adjustbox}\n\n\n Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,  ","latexPackages":["\\usepackage{tikz}","\\usetikzlibrary{quantikz}","\\usepackage{adjustbox}"],"output":"svg", "varwidth":1.2 }
```

### Notes:
* LatexRenderer creates a new latex document with the following structure:
  ```latex
  \documentclass{standalone/article}  
  INSERT SENT LATEXPACKAGES  
  \begin{document}  
  INSERT SENT CONTENT  
  \end{document}  
  ```

  This means that all declarations etc. that need to be inserted before `\begin document` must be included in latexPackages.
* The content and latexPackages must strictly follow the JSON guidelines. This means that all backslashes, doublequotes, newlines etc. must be escaped. Furthermore, is it required that the content of one field is within one line.
* Necessary newlines must explicitly be defined with \n. For example in a listing or after a comment: *$a^n$ %this is important **\n***.
* All cutout formats (svg, pdf, png, jpg) use `standalone` to crop the content. Standalone uses horizontal mode, which doesn't allow for lists or paragraphs. If you want to render lists, tables etc. use the fullPdf export option.
* Equations can be rendered as `standalone` when using $-Signs to indicate the math environment.
