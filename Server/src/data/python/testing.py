def function(x, word):
    y = len(word)
    if x < y:
        return (word + str(x))
    else:
        return word
print(function(3,'word'))