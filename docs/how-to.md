# How To
Instructions for developers.

## <a name="toc">Table of Contents</a>

* [How to perform initial setup for the module?](#ht-setup-module)
* [How to lint kotlin code with ktlint?](#ht-lint-kotlin)
* [How to make Intellij IDEA's built-in formatter produce 100% ktlint-compatible code?](#ht-idea-ktlint)

### <a name="ht-setup-module">How to perform initial setup for the module?</a>
First of all, make sure you initialized submodules of the module as described [here](https://docs.google.com/document/d/1kRbFx_9Za-PJGbIjeszHv42HXnjmnzRYPJVwUonUlK8/edit#heading=h.9pql0ux3bhfc) in our onboarding notes.

Then execute the [do-setup-module.py](../commons/do-setup-module.py) script. It will perform all the required initialization.

[TOC](#toc)

### <a name="ht-lint-kotlin">How to lint kotlin code with ktlint?</a>
It is automatically linted at verify phase of maven's lifecycle.
To execute `ktlint` separately run [do-ktlint.py](../do-ktlint.py) script.
Report will be printed to both the console and the [target/ktlint-report.txt](../target/ktlint-report.txt) file.

[TOC](#toc)

### <a name="ht-idea-ktlint">How to make Intellij IDEA's built-in formatter produce 100% ktlint-compatible code?</a>
Run [do-ktlint-apply-to-idea-project.py](../do-ktlint-apply-to-idea-project.py) script.

If for any reason it doesn't work, follow instructions described [here](https://github.com/shyiko/ktlint#-with-intellij-idea) (see option #2).

[TOC](#toc)
