import { Component, ElementRef, HostListener, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { WebsocketService } from '../services/websocket/websocket.service';
import { interval, map, merge, Observable, Observer, of, startWith, Subject, Subscription } from 'rxjs';
import { LoggedUser, LoggedUserService } from '../services/login/logged-user.service';
import { HttpRequestService } from '../services/http/http-request.service';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

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
    private sanitizer: DomSanitizer,
    private renderer: Renderer2) {
    // var message1 = new Message()
    // message1.sender = "Pan Danilecki";
    // message1.own = false;
    // message1.messa = "Zrobiliście już? >:C";
    // message1.send_date = new Date('2024-08-05T12:00:00Z');

    // var message2 = new Message()
    // message2.sender = "Michał";
    // message2.own = false;
    // message2.messa = "A gdzie tam XDDD";
    // message2.send_date = new Date('2024-08-07T14:00:00Z');

    // this.messages.push(message1);
    // this.messages.push(message2);
  }

  ngOnInit() {
    this._initCurrentDate();
    this.loggedUserService.user$.subscribe(user => {
      if(user) {
        this.loggedUser = user;
        this._initChat();
        this._subscribeWebsocket();
      }
    });
  }

  _initChat() {
    this.http.getGeneric<MessageRest[]>(`api/chat/${this.chatId}/init-chat`).subscribe(messagesRest => {
      messagesRest.forEach(messageRest => {
        const message = new Message();
        message.attachments = [];
        message.messa = messageRest.content;
        message.own = this.loggedUser.id == messageRest.creatorId;
        message.send_date = new Date(messageRest.createTime);
        message.sender = messageRest.creatorName;
        messageRest.files.forEach(fileRest => {
          const attachment = new Attachment(fileRest.name);
          attachment.fileId = fileRest.id;
          this._setAttachmentUrl(attachment);
          message.attachments.push(attachment);
          this.attachments.push(attachment);
        });
        this.messages.push(message);
      });
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
          attachment.fileId = file.fileId;
          this._setAttachmentUrl(attachment);
          attachments.push(attachment);
          this.attachments.push(attachment);
        });
        message.attachments = attachments;
        this.messages.push(message)
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
      this._setAttachmentUrl(attachment);
      this.attachments_to_send.push(attachment);
    }
  }

  _setAttachmentUrl(attachment: Attachment) {
    attachment.fileURL = null;
    if (!this._isImage(attachment)) {
      return;
    }
    if(attachment.fileId) {
      const headers = this.http.getHttpHeaders()
      if(headers) {
        this.httpClient.get(`http://localhost:8080/api/chat/file/${attachment.fileId}/img`, {headers: headers, responseType: 'blob'}).subscribe(blob => {
          const objectURL = URL.createObjectURL(blob);
          attachment.fileURL = objectURL;//this.sanitizer.bypassSecurityTrustUrl(objectURL);
        });
      }
      return;
    }
    if(attachment.file) {
      const reader = new FileReader();
      reader.onload = () => {
        attachment.fileURL = reader.result;
      }
      reader.readAsDataURL(attachment.file);
    }
  }

  _isImage(attachment: Attachment): boolean {
    const extension = attachment.name.split('.').pop()?.toLowerCase() as string;
    return ['png', 'jpg', 'jpeg', 'gif'].includes(extension);
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
      a.download = attachment.name; 
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
  fileURL: string | ArrayBuffer | null = null;
  fileId: number | null;

  public constructor(name: string){
    this.name = name;
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

interface MessageRest {
  creatorId: number,
  creatorName: string,
  createTime: string,
  content: string,
  files: FileRest[]
}

interface FileRest {
  id: number,
  name: string,
}
