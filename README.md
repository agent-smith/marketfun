marketfun
=========

Playing around with using technical indicators to look for buy opportunities for any asset on NYSE and NASDAQ.

Sample run for TechnicalsFinder:
-mssk 30 -ssk 14 -ssd 3 -tp 10 -bbp 20 -bbd 2.0 -epp 0.05 -mp 9.50 -mpf 30.0 -mv 400000.0 -debug

^ Will find all buy opportunities for the criteria for a trading period:
  1) Over the past 10 days that is breaks above the lower/uppper Bollinger Bands (20,2)
  2) Where the current Slow Stochastic %K is below 30 and above the current %D (where %K(14) and %D(3) is used to caclulate the Slow Stochastics)
  3) Having a maximum trading price of 9.50 per share
  4) With a minimum trading volume of 400K

