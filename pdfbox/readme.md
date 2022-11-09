# PDFBOX Example

This is an example of PDFBOX obfuscation. It does a text (chars) skipping during write process, this the text is not available in the new pdf At all.
[PDFBOX](https://pdfbox.apache.org/)

Usage:
`java string-to-replace`

## Issues found:
It's very sensitive to the content, where some can be easily detected and wiped out, some almost impossible, based on the chars chunks that are
based on: [Example](https://stackoverflow.com/questions/63592078/replace-or-remove-text-from-pdf-with-pdfbox-in-java#63615499)
