import os
dircont=os.listdir()
os.system("echo \"<!DOCTYPE html><html><head><head><body><a href=\"https://www.testdomainfall22.com\" target=\"_self\">Contents of Directory</a><p> > dircont.html\"")
print("doctype")
os.system(f"echo \"\n{dircont}\">> dircont.html")
print("arr")
os.system("echo \"</p></body></html> >> dircont.html\"")
print("end of file")