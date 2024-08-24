import { Component, ElementRef, HostListener, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { WebsocketService } from '../services/websocket/websocket.service';
import { interval, map, merge, Observable, Observer, of, startWith, Subject, Subscription } from 'rxjs';
import { LoggedUser, LoggedUserService } from '../services/login/logged-user.service';
import { HttpRequestService } from '../services/http/http-request.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  @ViewChild('endOfMessages') endOfMessages: ElementRef | undefined;
  @ViewChild('messagesScroll') messagesScroll: ElementRef | undefined;
  @Input() chatId: number = 1;

  private messagesScrollListener: (() => void) | undefined;

  message_text: string = "";
  messages: Message[] = []
  attachments_to_send :Attachment[] = []
  
  messageAddedSubject = new Subject<void>();
  currentDate$: Observable<Date>;

  loggedUser: LoggedUser;
  chatSubscription?: Subscription;

  users = [
    {
      name: "user1"
    },
    {
      name: "user2"
    },
    {
      name: "user3"
    },
    {
      name: "user4"
    },
    {
      name: "user5"
    },
    {
      name: "user6"
    }
  ]

  attachments: Attachment[] = []

  constructor(private loggedUserService: LoggedUserService, 
    private http: HttpRequestService,
    private httpClient: HttpClient,
    private websocketService: WebsocketService, 
    private renderer: Renderer2) {
    var message1 = new Message()
    message1.sender = "Pan Danilecki";
    message1.own = false;
    message1.messa = "Zrobiliście już? >:C";
    message1.send_date = new Date('2024-08-05T12:00:00Z');

    var message2 = new Message()
    message2.sender = "Michał";
    message2.own = false;
    message2.messa = "A gdzie tam XDDD";
    message2.send_date = new Date('2024-08-07T14:00:00Z');

    this.messages.push(message1);
    this.messages.push(message2);
  }

  ngOnInit() {
    this._initCurrentDate();
    this.loggedUserService.user$.subscribe(user => {
      if(user) {
        this.loggedUser = user;
        this._subscribeWebsocket();
      }
    });
  }

  _initCurrentDate() {
    const timer$ = interval(30000).pipe(
      map(() => new Date())
    );
    const messageAdded$ = this.messageAddedSubject.pipe(
      map(() => new Date())
    );
    this.currentDate$ = merge(timer$, messageAdded$).pipe(
      startWith(new Date())
    );
  }

  _subscribeWebsocket() {
    this.websocketService.connect();
    this.websocketService.getConnectedObservable().subscribe(() => {
      this.websocketService.subscribe<MessageResponseWs>(`/topic/chat/${this.chatId}/messages`).subscribe(messageWS => {
        var sendDate = new Date(messageWS.createTime);
        let message = new Message();
        message.sender = messageWS.senderName;
        message.own = this.loggedUser.id == messageWS.userId;
        message.messa = messageWS.message;
        message.send_date = sendDate;

        const attachments: Attachment[] = [];
        messageWS.files.forEach(file => {
          const attachment = new Attachment(file.fileName);
          attachment.canBeDisplayed = true;
          attachment.fileId = file.fileId;
          attachments.push(attachment);
        })
        message.attachments = attachments;
        this.messages.push(message)
        
        if (message.attachments.length > 0){
          Array.prototype.push.apply(this.attachments, message.attachments);
        }
        this.messageAddedSubject.next();
      });
    });
  }

  ngAfterViewInit() {
    if (this.endOfMessages) {
      this.endOfMessages.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }
    if (this.messagesScroll){
      this.messagesScrollListener = () => {
        const container = this.messagesScroll!.nativeElement as HTMLElement;
        if (container.scrollTop === 0){
          // Miejsca na obsługę łądowania dynamicznego wiadomości
          console.log("XD")
        }
      };
      this.messagesScroll.nativeElement.addEventListener('scroll', this.messagesScrollListener);
    }
  }

  ngOnDestroy(): void {
    if (this.messagesScroll && this.messagesScrollListener) {
      this.messagesScroll.nativeElement.removeEventListener('scroll', this.messagesScrollListener);
    }
    this.chatSubscription?.unsubscribe();
  }

  sendMessage() {
    if (this.message_text.length === 0 && this.attachments_to_send. length === 0) {
      return;
    }
    this.uploadFile(this.attachments_to_send[0]).subscribe(messageIdOrNull => {
      const messageRequest: MessageRequestWs = {userId: this.loggedUser.id, message: this.message_text, messageId: messageIdOrNull};
      this.websocketService.send(`/app/chat/${this.chatId}/send-message`, messageRequest);
      this.attachments_to_send = [];
      this.message_text = "";
    });
  }
  sendMessageWithFile() {

  }

  uploadFile(attachment: Attachment): Observable<number | null> {
    if(!attachment || !attachment.file) {
      return of(null);
    }
    const headers = this.http.getHttpHeaders();
    if(!headers) {
      return of(null);
    }
    const file: File = attachment.file;
    const formData = new FormData();
    formData.append("file", file);
    return this.httpClient.post<number | null>(`http://localhost:8080/api/chat/${this.chatId}/file/upload`, formData, {headers: headers});
  }

  sendMessageArea(event: KeyboardEvent) {
    if (event.key === 'Enter' && event.shiftKey){
      event.preventDefault();
      const textarea = document.querySelector('textarea');
      if (textarea) {
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        this.message_text = this.message_text.substring(0, start) + '\n' + this.message_text.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 1;
      }
    }
    else if (event.key === 'Enter') {
      event.preventDefault();
      if (this.message_text.length === 0 && this.attachments_to_send. length === 0)
        return;
  
      let message = new Message();
      message.sender = "you";
      message.own = true;
      message.messa = this.message_text;
      message.attachments = this.attachments_to_send;
  
      this.messages.push(message)
  
      if (message.attachments.length > 0){
        Array.prototype.push.apply(this.attachments, message.attachments);
      }

      const textarea = document.querySelector('textarea');
      if (textarea) {
        textarea.value = "";
        this.message_text = "";
      }
  
      this.attachments_to_send = [];
      this.message_text = "";
      this.message_text = "";
    }
  }

  ClearText() {
    this.message_text = "";
  }

  removeAttachment(attach: Attachment) {
    this.attachments_to_send = this.attachments_to_send.filter(a => a !== attach);
  }

  onFileChanged(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      let attachment = new Attachment(file.name);
      attachment.file = file;
      const reader = new FileReader();
      const fileExtension = file.name.split('.').pop()?.toLowerCase() as string;

      if (['png', 'jpg', 'jpeg', 'gif'].includes(fileExtension)) {
        attachment.canBeDisplayed = true;
        reader.onload = () => {
          attachment.fileURL = reader.result;
        }
        reader.readAsDataURL(file);
      }
      else {
        attachment.canBeDisplayed = false;
        attachment.fileURL = null;
      }

      this.attachments_to_send.push(attachment);
    }
  }

  downloadFile(attachment: Attachment) {
    const headers = this.http.getHttpHeaders();
    if(!headers) {
      return;
    }
    headers.set('Accept', 'application/octet-stream');
    return this.httpClient.get(`http://localhost:8080/api/chat/file/${attachment.fileId}/download`, {headers: headers, responseType: 'blob'}).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = attachment.name; // Ustaw nazwę pliku
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    });
  }

}

class Attachment {
  name: string;
  file: File;
  extension: string;
  canBeDisplayed: boolean;
  fileURL: string | ArrayBuffer | null = null;
  fileId: number | null;

  public constructor(name: string){
    this.name = name;
    this.canBeDisplayed = false;
  }
}

class Message {
  sender: string;
  messa: string;
  own: boolean;
  send_date: Date;
  attachments: Attachment[];
}

interface MessageRequestWs {
  userId: number,
  messageId: number | null,
  message: string,
}

interface MessageResponseWs {
  userId: number,
  senderName: string,
  message: string,
  createTime: string,
  files: FileWs[],
}

interface FileWs {
  fileId: number,
  fileName: string,
}
