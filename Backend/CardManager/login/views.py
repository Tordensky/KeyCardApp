
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.http import HttpResponse

import json

def loginToServer(request):
    username = request.GET.get('user')
    password = request.GET.get('password')
    print username, password
    user = authenticate(username=username, password=password)
    print  user
    if user is not None:
        if user.is_active:
            #log in user
            login(request,user)
            return HttpResponse(status=200)
        else:
            print 'hmm something went wrong'
            # Return a 'disabled account' error message
    else:
        print 'user is not authenticated'
        return HttpResponse(status=403)
        # Return an 'invalid login' error message.
    
    print user, password
    
    
    
def newUser(request):
    if request.method == 'POST':
        try:
            credentials = json.loads(request.body, encoding = 'latin1')
            
   
            print credentials['username']
            print credentials['password']
            name = credentials['username'].lower()
            password = credentials['password']
            
            print name, password
            
            # test if username exists
            checkUser = User.objects.filter(username = name)
            if checkUser.exists():
                print "USERNAME EXISTS"
                return HttpResponse(status=409)
            else:         
                user = User.objects.create_user(name, None, password)
                user.is_staff = False
                user.is_active = True
                user.is_superuser = False
                user.save()
                print "SUCCESSFULLY CREATED USER" 
                return HttpResponse(status=200)
                        
        except Exception as e:
            print e
            return HttpResponse(status=500)
           
        
def logoutFromServer(request):
    print "LOGGING OUT"
    logout(request)
    return HttpResponse(status=200)
    
    
    
    
    
    
    