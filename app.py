import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from tkinter import simpledialog
import webbrowser

class iCloudAltApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("AltCloud")
        self.geometry("700x580")
        self.resizable(False, False)

        # Load Azure theme
        self.tk.call("source", "theme/azure.tcl")
        self.tk.call("set_theme", "dark")

        # Center frame
        self.container = ttk.Frame(self)
        self.container.place(relx=0.5, rely=0.5, anchor="center")

        self.create_widgets()

        # Prompt for iFetch login if not already set
        if not self.detect_ifetch_login():
            self.prompt_ifetch_login()

    def create_widgets(self):
        row = 0

        self.license_status = tk.StringVar(value="Free")
        ttk.Label(self.container, text="License:").grid(row=row, column=0, sticky="e", padx=10, pady=5)
        self.license_label = ttk.Label(self.container, textvariable=self.license_status, foreground="green")
        self.license_label.grid(row=row, column=1, sticky="w")
        row += 1

        # Services
        ttk.Label(self.container, text="Select Services:").grid(row=row, column=0, sticky="ne", padx=10, pady=5)
        services_frame = ttk.Frame(self.container)
        services_frame.grid(row=row, column=1, sticky="w")
        row += 1

        self.services = {
            "iDrive": tk.BooleanVar(),
            "Photos": tk.BooleanVar(),
            "Calendar": tk.BooleanVar(),
            "Passwords": tk.BooleanVar(),
        }

        for i, (name, var) in enumerate(self.services.items()):
            cb = ttk.Checkbutton(services_frame, text=name, variable=var)
            cb.grid(row=i, column=0, sticky="w", pady=2)
            if name == "iDrive":
                ttk.Button(services_frame, text="Config", command=self.config_idrive).grid(row=i, column=1, padx=5)

        # Destination
        ttk.Label(self.container, text="Destination Path:").grid(row=row, column=0, sticky="e", padx=10, pady=5)
        self.path_label = ttk.Label(self.container, text="/backup/path", relief="sunken", width=40)
        self.path_label.grid(row=row, column=1, sticky="w")
        ttk.Button(self.container, text="Change", command=self.change_path).grid(row=row, column=2, padx=5)
        row += 1

        # Tabs for monetization
        notebook = ttk.Notebook(self.container)
        notebook.grid(row=row, column=0, columnspan=3, pady=10)

        self.tab_cpu = ttk.Frame(notebook)
        self.tab_ads = ttk.Frame(notebook)
        self.tab_premium = ttk.Frame(notebook)

        notebook.add(self.tab_cpu, text="CPU Support")
        notebook.add(self.tab_ads, text="Ads Support")
        notebook.add(self.tab_premium, text="Premium")

        # CPU Tab
        ttk.Label(self.tab_cpu, text="We'll use ~5% of your CPU to mine crypto while backing up.").pack(padx=10, pady=10)

        # Ads Tab
        ttk.Label(self.tab_ads, text="You'll watch an ad every 15 minutes or per 50GB uploaded.").pack(padx=10, pady=10)

        # Premium Tab
        ttk.Label(self.tab_premium, text="3€/month subscription. Cancel anytime.").pack(padx=10, pady=(10, 2))
        ttk.Label(self.tab_premium, text="License Key:").pack()
        self.license_entry = ttk.Entry(self.tab_premium, width=30)
        self.license_entry.pack()

        ttk.Label(self.tab_premium, text="Email:").pack(pady=(5, 2))
        self.email_entry = ttk.Entry(self.tab_premium, width=30)
        self.email_entry.pack()

        # Subscribe Link
        subscribe_btn = ttk.Button(self.tab_premium, text="Subscribe here", command=self.open_subscription_link)
        subscribe_btn.pack(pady=(10, 0))

        # Start Button
        row += 1
        self.start_button = ttk.Button(self.container, text="Start Backup", command=self.start_backup)
        self.start_button.grid(row=row, column=0, columnspan=3, pady=10)

        # Progress
        row += 1
        self.progress = ttk.Progressbar(self.container, orient="horizontal", length=400, mode="determinate")
        self.progress.grid(row=row, column=0, columnspan=3, pady=5)

        # Estimation (static display as per your request)
        row += 1
        self.status_label = ttk.Label(self.container, text="15.4 / 400 GB, 15 min, 18 MB/s")
        self.status_label.grid(row=row, column=0, columnspan=3)

    def config_idrive(self):
        messagebox.showinfo("iDrive Config", "Open iDrive Configuration Window")

    def change_path(self):
        new_path = filedialog.askdirectory()
        if new_path:
            self.path_label.config(text=new_path)

    def open_subscription_link(self):
        webbrowser.open("https://your-lemonsqueezy-product-url.com")  # ← Replace with your real LemonSqueezy link

    def start_backup(self):
        selected = [k for k, v in self.services.items() if v.get()]
        if not selected:
            messagebox.showwarning("No Services Selected", "Please select at least one service to backup.")
            return

        self.progress["value"] = 0
        self.after(300, lambda: self.simulate_progress(0))

    def simulate_progress(self, step):
        if step <= 100:
            self.progress["value"] = step
            self.after(100, lambda: self.simulate_progress(step + 5))

    def detect_ifetch_login(self):
        # Placeholder: Replace with real login check logic
        return False  # Simulate that login is not set

    def prompt_ifetch_login(self):
        # Create a modal blocker for login
        login_window = tk.Toplevel(self)
        login_window.title("iFetch Login Required")
        login_window.geometry("400x200")
        login_window.grab_set()  # Make it modal
        login_window.transient(self)

        tk.Label(login_window, text="iFetch Login", font=("Segoe UI", 12, "bold")).pack(pady=10)
        tk.Label(login_window, text="Username:").pack()
        username_entry = ttk.Entry(login_window, width=30)
        username_entry.pack()

        tk.Label(login_window, text="Password:").pack()
        password_entry = ttk.Entry(login_window, width=30, show="*")
        password_entry.pack()

        def submit_login():
            username = username_entry.get()
            password = password_entry.get()
            # Add your authentication logic here
            if username and password:
                login_window.destroy()
            else:
                messagebox.showwarning("Input Required", "Please enter both username and password.")

        ttk.Button(login_window, text="Login", command=submit_login).pack(pady=15)

if __name__ == "__main__":
    app = iCloudAltApp()
    app.mainloop()