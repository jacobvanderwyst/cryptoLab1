import os

path="/var/www/html/"
filename="server"
os.system(f"javac -d . {path}cryptoLab1/{filename}.java")
os.system(f"jar cvf {path}cryptoLab1/{filename}.jar {path}cryptoLab1/{filename}.class")
os.system(f"echo \"Main-Class: {filename}\" > MANIFEST.MF")
os.system(f"jar cvmf MANIFEST.MF {filename}.jar {filename}.class")
os.system(f"sudo chmod 777 {filename}.jar")
os.system("tree")
os.system(f"sudo mv /var/www/html/cryptoLab1/{filename}.jar /var/www/html/")