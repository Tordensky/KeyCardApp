from django.contrib.auth import authenticate, login
from django.http import HttpResponse
import base64

import json

def loginToServer(request):
    print request
    username = request.GET.get('user')
    password = request.GET.get('password')
    print username, password
    user = authenticate(username=username, password=password)
    print  user
    if user is not None:
        if user.is_active:
            #log in user
            login(request,user)

            #request.session['has_commented'] = True
            #print 'user logged in'
            return HttpResponse(status=200)
            # Redirect to a success page.
        else:
            print 'hmm something went wrong'
            # Return a 'disabled account' error message
    else:
        print 'user is not authenticated'
        return HttpResponse(status=403)
        # Return an 'invalid login' error message.
    
    print user, password
#    for key, value in request.META.iteritems():
#        print key, value
#
#    
#    if 'Authorization' in request.META:
#        print "yes"
#        auth = request.META['Authorization'].split()
#        if len(auth) == 2:
#            # NOTE: We are only support basic authentication for now.
#            #
#            if auth[0].lower() == "basic":
#                uname, passwd = base64.b64decode(auth[1]).split(':')
#                print uname
#                passwd
#    else:
#        print "NO"
#                
#    print request.META
#    dict = json.loads(request.body)
#    
#    username = dict['username']
#    password = dict['password']
#    
#    print username
#    print password
#    user = authenticate(username=username, password=password)
#    #print  user
#    if user is not None:
#        if user.is_active:
#            #log in user
#            a = login(request,user)
#
#            #request.session['has_commented'] = True
#            #print 'user logged in'
#            return HttpResponse(status=200)
#            # Redirect to a success page.
#        else:
#            print 'hmm something went wrong'
#            # Return a 'disabled account' error message
#    else:
#        print 'user is not authenticated'
#        return HttpResponse(status=403)
#        # Return an 'invalid login' error message.