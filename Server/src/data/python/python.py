x = 2
y = 12
def compute_gcd(x,y):
    while(y):
        x, y = y, x % y
    return x
def compute_lcm(num1, num2):
    lcm = (x*y)//compute_gcd(x,y)
    return lcm
print("The L.C.M. is", compute_lcm(x, y))