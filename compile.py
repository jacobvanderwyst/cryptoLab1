import os

path="/var/www/html/"
filename="server"
os.system("sudo rm -rf /var/www/html/cryptoLab1")
os.system("sudo rm -f {path}server.jar")
os.system("sudo rm -f {path}MANIFEST.MF")
os.system("sudo rm -f {path}sources.txt")
os.system("cd /var/www/html && git clone https://github.com/jacobvanderwyst/cryptoLab1.git")
os.system("find -name \"*.java\" > sources.txt")
os.system(f"javac @sources.txt")
os.system(f"echo \"Main-Class: {filename}\" > MANIFEST.MF")
os.system(f"jar cvmf MANIFEST.MF {filename}.jar {path}cryptoLab1/makeServerSockets.class {path}cryptoLab1/server.class")
os.system(f"sudo chmod 777 {filename}.jar")
os.system("tree")
#os.system(f"sudo mv /var/www/html/cryptoLab1/{filename}.jar /var/www/html/")