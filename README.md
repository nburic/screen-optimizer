# Screen optimizer
This is an example of displaying article text on square LED display of various screen width and height. The customer wants the buyer scanning the product bar code to display the name, price and description of the product being scanned on the LED screen. All items scanned by the user must be displayed on the screen. Different stores in Slovenia have screens of different sizes, but all are rectangular. The width and height of the screen are listed below, along with the text to be displayed on the screen. In order to take advantage of the full screen, it is necessary to convert how many pixels a single character of the text displayed on the screen can be without dividing any word. All characters must have the same width and height (eg 'l' and 'm' occupy the same horizontal space as the whitespace). Font characters are the same width and height, so there is no extra space between adjacent characters or adjacent lines.

# Input
Each line of the input file contains one 'W H T' test case. W is the width and H the height of the screen; T is the text that should appear on the LED screen.

# Output
Output is the character size. If the text cannot be displayed output is 0.
