===============================================
Generic VXML IVR Provider Demo: Receiving Calls
===============================================

    You accept phone calls, prompt for and record a code, and then send the code in an SMS to the caller.

    In more details:

    #. You call the IVR provider [#]_.
    #. The IVR provider calls Motech [#]_ to request the ``hello`` VXML template which will tell it what to do. This
       demo uses VXML but, depending on the provider, it could also be CCXML or some proprietary language.
    #. Motech returns the requested ``hello`` VXML template.
    #. The IVR provider executes the instructions in ``hello`` which prompt the caller to type in  a number.
    #. The caller (that's you!) types in a number.
    #. The IVR provider sends the number to Motech along with a request for the ``thankyou`` template.
    #. Motech receives the number you typed, sends a Motech Event, and returns the requested ``thankyou`` template.
    #. The IVR provider executes the instructions in ``thankyou``: say 'Thank you' and hang up.
    #. Meanwhile, on the Motech side, the Tasks module was waiting for a *[we received a template request and the value
       of the* ``callStatus`` *parameter is ANSWERED]* [#]_ Motech Event. When it finally receives it,
       it extracts the number you typed, creates a 'You chose *x*' message and sends an SMS [#]_ to the number that
       originated the call, which it also receives as a standard parameter
       from the IVR provider.
    #. You receive 'You picked *x*' SMS. Nifty, eh?

    .. [#] IVR providers will typically give you a phone number you can use to call your application on their system.
           They also will require that you give them a publicly accessible URL so they cal request the VXML that you
           want to be executed when a call is received at this number.
    .. [#] More precisely makes a REST call to the IVR module at the ``/template`` HTTP endpoint.
    .. [#] To differentiate it from other template requests.
    .. [#] From the SMS provider you configured.


IVR Provider
------------

    For this demo we used `Voxeo <http://evolution.voxeo.com/>`_, a generic VXML/CCXML provider.

IVR Settings
------------

    We need to create an IVR Config so the IVR module knows what to do when it receives HTTP requests from IVR
    providers. Click on **Modules** / **IVR** / **Settings**:

    .. image:: img/in_settings.png
        :scale: 100 %
        :alt: IVR Demo - IVR Provider Config for incoming calls
        :align: center

    .. note::
        Because we're using the Voxeo IVR provider we named our config **voxeo**. The name isn't important,
        but it's a good idea to use a simple one as it'll be part of the URL you'll provide your IVR provider so it
        knows where to get the VXML.

A little VXML
-------------

    Here's the ``hello`` VXML template:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
          <vxml version = "2.1">
          <form id="enterCode">
            <field name="code" type="digits?minlength=1;maxlength=1">
              <prompt>
                Hello! Please pick a number between 0 and 9.
              </prompt>
            </field>
            <filled>
              <prompt>
                You picked <value expr="code" />.
              </prompt>
              <assign name="from" expr="session.callerid" />
              <assign name="providerCallId" expr="session.sessionid" />
              <assign name="callStatus" expr="'ANSWERED'" />
              <submit name="sendCode" next="http://zebra.motechproject.org:8080/motech-platform-server/ivr/template/voxeo/thankyou" namelist="code from providerCallId callStatus" method="get" />
            </filled>
          </form>
        </vxml>

    .. note::
        The selected number is passed as one of query parameters (``code``) the IVR provider sends along with its
        request for the ``thankyou`` template.

    And the ``thankyou`` VXML template, which simply says 'Thank you' and hangs up:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <vxml version = "2.1" >
          <form>
            <block>
              <prompt>
                Thank you
              </prompt>
            </block>
          </form>
        </vxml>


Creating the Templates
----------------------

    To add these two templates, click **Module** / **IVR** / **Settings** and then **+ Add Template**:

    .. image:: img/in_templates.png
        :scale: 100 %
        :alt: IVR Demo - Templates
        :align: center


Creating the `Task <tasks>`_
----------------------------

    We need to create a task where the trigger is an IVR template request where the call status is ``ANSWERED`` and the
    action is to send an SMS to the original caller with the code she entered in the message:

    .. image:: img/in_task.png
        :scale: 100 %
        :alt: IVR Demo - Creating a task
        :align: center

    .. note:: **code** is extracted from the Motech event payload with ``{{trigger.provider_extra_data.code}}``

    .. note::
        A **+1** is added to the SMS recipient because our sample SMS provider, `Plivo <http://plivo.com/>`_, needs it.


Et Voila!
---------

    Now call your application at the phone number that your IVR provider gave you, then listen to the "Hello! Please
    pick a number between 0 and 9." prompt, type in a number (say 8). The IVR system will say "You picked 8. Thank you",
    then the call will disconnect and soon enough you should receive an SMS:

    .. image:: img/in_sms.png
        :scale: 100 %
        :alt: IVR Demo - Receiving an SMS
        :align: center


Did it work?
------------

    In addition to the obvious sign that you're receiving an SMS from your SMS provider,
    there are other ways you can check your application works.

    * You can look at the Tasks module's **Recent task activity** list to see if your task was executed:

        .. image:: img/in_recent_task_activity.png
            :scale: 100 %
            :alt: IVR Demo - Recent task activity
            :align: center

    * Or you can look at your task's history:

        .. image:: img/in_task_history.png
            :scale: 100 %
            :alt: IVR Demo - Task History
            :align: center

    * You can also browse the IVR CallDetailRecord entity in the database using the MDS Data Browser:

        .. image:: img/in_cdr.png
            :scale: 100 %
            :alt: IVR Demo - CallDetailRecord
            :align: center

        .. note::
            Our simple VXML application did not bother to set the CallDirection nor many other fields in
            its status callback to Motech.

    * Yet another way to see how your application would be to be to look at the SMS log or, for even more details,
      the Server Log.