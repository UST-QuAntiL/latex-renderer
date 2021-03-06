# LaTex-Renderer
Service for rendering latex content such as algorithms, diagrams, pictures etc. Latex-Render accepts the document body, the packages that are required to render the document and offers svg, png and pdf as output formats.

### Version:
The latest version is: **0.1**


### Installation:
  * As a **Docker Container** (recommended)
	1. Pull and Run image via: *docker run --rm -p 8082:8082 planqk/latex-renderer:latest

  * As a **Spring-Boot** project:

	Requirements:
  	  * Full latex distribution (for example miktex)
  	  * For SVG support: Installation of Poppler for Windows and Path setup of pdf2svg2.bat  https://community.jalios.com/jcms/jc2_183627/en/pdf2svg2-bat-script
 	  * For PNG support: Ghostscript 
		* The directory containing gswin32c.exe shall be added to PATH.
		* ImageMagick (including legacy scripts) - rename convert.exe to imgconvert.exe 

	Install:
  	  1. Download Latex-Renderer
  	  2. Make sure all required programms are installed.
  	  3. 1st time Setup: *mvn package -DskipTests* 
  	  4. To start Latex-Renderer run: *mvn spring-boot:run*
	
### API:
Runs on Port 8082.

http://localhost:8082/renderLatex  expects (String content, List<String> latexPackages, String output) as JSON

Export formats are: 
  * pdf: returns a PDF document that contains a cutout of the rendered content.
  * fullPdf: returns a PDF document that contains the rendered content on ISO A4 pages.
  * png: returns a PNG that contains the cutout of the rendered content.
  * svg: returns a SVG that contains the cutout of the rendered content.
  * svgz: returns a zipped SVG that contains the cutout of the rendered content.

Example:  
``\begin{quantikz}
\lstick{\ket{0}} & \phase{\alpha} & \gate{H}
& \phase{\beta} & \gate{H} & \phase{\gamma}
& \rstick{Arbitrary\\pure state}\qw
\end{quantikz}``

is included in the REST call via
``{"content":"\\\begin{quantikz}\\n\t\\\lstick{$\\\ket{0}$} & \\\gate{H} & \\\ctrl{1} & \\\gate{H} & \\\ctrl{1} & \\\swap{2} & \\\ctrl{1} & \\\qw \\\\\\\ \n \t \\\lstick{$\\\ket{0}$} & \\\gate{D} & \\\targ{} & \\\octrl{-1} & \\\control{} & \\\qw & \\\octrl{1} & \\\qw \\\\\\\\ \n \t &&&&& \\\targX{} & \\\gate{F} & \\\qw \n \\\end{quantikz}",  
"latexPackages":["\\\usepackage{tikz}","\\\usetikzlibrary{quantikz}"],  
"output":"svg"}``

### Userguide:
  * It is recommended to use this service for automated latex rendering. 
  * The Latex-Renderer can render maths, but its recommended to use Mathjax or Katex for maths in Webapplications.
  * When sending content always make sure that all required packages are included in the *latexPackages* field. 
  *latexPackages* expects an array of declarations. Therefore list your packages and other declarations in for example an ArrayList.
  * The *content* string should be prepared properly before sending so the latex compiler won't have any issues because of missing spaces or linebreaks. Follow the instructions in the *Notes* section to make sure your latex string works.
  


### Notes:
  * LatexRenderer creates a new latex document with the following structure: 
    \documentclass{standalone/article}  
    INSERT SENT LATEXPACKAGES  
    \begin{document}  
    INSERT SENT CONTENT  
    \end{document}  
    This means that all declarations etc. that need to be inserted before `\begin document` must be included in latexPackages.
  * The content and latexPackages must strictly follow the JSON guidelines. This means that all Backslashes, Doublequotes, Newlines etc. must be escaped. Furthermore is it required that the content of one field is within one line.
  * Necessary Newlines must explicitly be defined with \n. For example in a listing or after a comment: *$a^n$ %this is important **\n***.
  * All cutout formats (pdf, png, svg, svgz) use `standalone` to crop the content. Standalone uses horizontal mode, which doesn't allow for lists or paragraphs. If you want to render lists, tables etc. use the fullPdf export option.
  * Equations can be rendered as `standalone` when using $-Signs to indicate the math environment.
